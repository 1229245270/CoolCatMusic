package com.hzc.coolcatmusic.ui.homefragment1;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.databinding.FragmentScanningMusicBinding;
import com.hzc.coolcatmusic.ui.costom.DownloadSeekBar;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.Messenger;

public class ScanningMusicFragment extends BaseFragment<FragmentScanningMusicBinding,ScanningMusicViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_scanning_music;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ScanningMusicViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(ScanningMusicViewModel.class);
    }

    public LottieAnimationView lottie;
    public SeekBar seekBarDuration,seekBarSize;
    public DownloadSeekBar downloadSeekBar;
    public LinearLayout llSearch,llSearchConfigure,llDownload,llScanning,llDownloaded;
    public RecyclerView recycleView;

    @Override
    public void initData() {
        super.initData();
        lottie = binding.lottie;
        seekBarDuration = binding.seekBarDuration;
        seekBarSize = binding.seekBarSize;
        downloadSeekBar = binding.downloadSeekBar;
        llSearch = binding.llSearch;
        llSearchConfigure = binding.llSearchConfigure;
        llDownload = binding.llDownload;
        llScanning = binding.llScanning;
        llDownloaded = binding.llDownloaded;
        recycleView = binding.recycleView;

        viewModel.setSeekBarText();
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewModel.minDuration = progress * 10;
                viewModel.setSeekBarText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewModel.minSize = progress * 100;
                viewModel.setSeekBarText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        viewModel.downloadStatus.setValue(ScanningMusicViewModel.DownloadStatus.UN_EXECUTED);
        viewModel.viewStatus.setValue(ScanningMusicViewModel.ViewStatus.DEFAULT);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        //开启搜索
        viewModel.scanEvent.observe(this,this::requestPermission);
        //按钮状态
        viewModel.downloadStatus.observe(this, this::changeDownloadStatus);
        //开启下载
        viewModel.downloadFileEvent.observe(this, this::changeDownloadFile);
        //界面状态
        viewModel.viewStatus.observe(this,this::changeViewStatus);
    }

    private final int PERMISSION_CODE = 1000;
    /**
     * 请求全局搜索权限
     */
    private void requestPermission(boolean b){
        // android 11  且 不是已经被拒绝，请求全局文件权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                startActivityForResult(intent, PERMISSION_CODE);
                return;
            }
        }
        startScan();
    }

    /**
     * 改变按钮状态
     */
    private void changeDownloadStatus(ScanningMusicViewModel.DownloadStatus downloadStatus){
        switch (downloadStatus){
            case UN_EXECUTED:
                downloadSeekBar.getTextView().setText("开始破解");
                downloadSeekBar.setOnClickListener(v -> {
                    viewModel.unlockSong();
                });
                break;
            case UNLOCKING:
                downloadSeekBar.getTextView().setText("破解中");
                downloadSeekBar.setOnClickListener(v -> {});
                break;
            case UNLOCK_SUCCESS:
                downloadSeekBar.getTextView().setText("开始下载");
                break;
            case UNLOCK_FAIL:
                downloadSeekBar.getTextView().setText("破解失败");
                downloadSeekBar.setOnClickListener(v -> {
                    viewModel.unlockSong();
                });
                break;
            case DOWNLOAD_SUCCESS:
                downloadSeekBar.getTextView().setText("下载完成");
                break;
            case DOWNLOAD_FAIL:
                downloadSeekBar.getTextView().setText("下载失败");
                downloadSeekBar.setOnClickListener(v -> {
                    viewModel.unlockSong();
                });
                break;
            default:
        }
    }

    public AtomicInteger downloadSize = new AtomicInteger();
    public AtomicInteger downloadSuccessSize = new AtomicInteger();
    public AtomicInteger downloadFailSize = new AtomicInteger();

    /**
     * 下载文件
     */
    private void changeDownloadFile(List<String> files){
        viewModel.viewStatus.setValue(ScanningMusicViewModel.ViewStatus.DOWNLOADING);
        File parentFile = new File(AppApplication.PATH_CACHE_SONG);
        File[] listFiles = parentFile.listFiles();
        List<DownloadTask> taskList = new ArrayList<>();
        for(int i = 0;i < files.size();i++){
            boolean isHave = false;
            String downloadUrl = files.get(i);
            if(listFiles != null && listFiles.length > 0){
                for(File file : listFiles){
                    if(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1).equals(file.getName())){
                        isHave = true;
                        break;
                    }
                }
            }
            if(!isHave){
                DownloadTask downloadTask = new DownloadTask.Builder(downloadUrl, parentFile)
                        .setMinIntervalMillisCallbackProcess(1000)
                        .setConnectionCount(1)
                        .build();
                taskList.add(downloadTask);
            }
        }
        DownloadTask[] tasks = taskList.toArray(new DownloadTask[0]);
        downloadSeekBar.getProgressBar().setMax(tasks.length);
        downloadSize.set(0);
        downloadSuccessSize.set(0);
        downloadFailSize.set(0);
        setDownloadScanResultText();
        if(tasks.length == 0){
            viewModel.downloadStatus.setValue(ScanningMusicViewModel.DownloadStatus.DOWNLOAD_SUCCESS);
            viewModel.viewStatus.setValue(ScanningMusicViewModel.ViewStatus.DOWNLOAD_END);
            return;
        }
        DownloadTask.enqueue(tasks, new DownloadListener4WithSpeed() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
            }

            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {
            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                downloadSize.getAndIncrement();
                int progress = (int) (downloadSize.get() * 1.0 / tasks.length * 100);
                if(realCause == null){
                    downloadSuccessSize.getAndIncrement();
                }else{
                    downloadFailSize.getAndIncrement();
                }
                downloadSeekBar.getTextView().setText(progress + "%");
                downloadSeekBar.getProgressBar().setProgress(downloadSuccessSize.get());
                if(downloadSize.intValue() == tasks.length){
                    viewModel.downloadStatus.setValue(ScanningMusicViewModel.DownloadStatus.DOWNLOAD_SUCCESS);
                    viewModel.viewStatus.setValue(ScanningMusicViewModel.ViewStatus.DOWNLOAD_END);
                }
                setDownloadScanResultText();
            }
        } );
    }

    /**
     * 设置下载进度文字
     */
    private void setDownloadScanResultText(){
        Spanned spanned = Html.fromHtml("" +
                "<font color='#000000'>总共下载</font>" +
                "<font color='" + viewModel.mainBg + "' size=30>" + downloadSize + "首</font>" +
                "<font color='#000000'>音乐，成功下载</font>" +
                "<font color='" + viewModel.mainBg + "' size=30>" + downloadSuccessSize + "首</font>" +
                "<font color='#000000'>，下载失败</font>" +
                "<font color='" + viewModel.redBg + "' size=30>" + downloadFailSize + "首</font>" +
                "",Html.FROM_HTML_MODE_COMPACT);
        viewModel.scanResultText.set(spanned);
    }

    /**
     * 改变界面状态
     */
    private void changeViewStatus(ScanningMusicViewModel.ViewStatus viewStatus){
        switch (viewStatus){
            case DEFAULT:
                llSearch.setVisibility(View.VISIBLE);
                llSearchConfigure.setVisibility(View.VISIBLE);
                llScanning.setVisibility(View.GONE);
                llDownload.setVisibility(View.GONE);
                llDownloaded.setVisibility(View.GONE);
                break;
            case SEARCHING:
                lottie.playAnimation();
                llSearch.setVisibility(View.VISIBLE);
                llSearchConfigure.setVisibility(View.GONE);
                llScanning.setVisibility(View.VISIBLE);
                llDownload.setVisibility(View.GONE);
                llDownloaded.setVisibility(View.GONE);
                break;
            case SEARCH_END:
            case DOWNLOAD_FAIL:
                lottie.pauseAnimation();
                llSearch.setVisibility(View.GONE);
                llSearchConfigure.setVisibility(View.GONE);
                llScanning.setVisibility(View.GONE);
                llDownload.setVisibility(View.VISIBLE);
                llDownloaded.setVisibility(View.GONE);

                recycleView.setVisibility(View.VISIBLE);
                break;
            case DOWNLOADING:
                lottie.playAnimation();
                recycleView.setVisibility(View.GONE);

                break;
            case DOWNLOAD_END:
                lottie.pauseAnimation();
                llSearch.setVisibility(View.GONE);
                llSearchConfigure.setVisibility(View.GONE);
                llScanning.setVisibility(View.GONE);
                llDownload.setVisibility(View.GONE);
                llDownloaded.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PERMISSION_CODE){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
                startScan();
            }
        }
    }

    @Override
    public void onDestroy() {
        Messenger.getDefault().sendNoMsg(LocalMusicViewModel.TOKEN_LOCAL_MUSIC_SET_RESULT);
        super.onDestroy();
    }

    private void startScan(){
        viewModel.startScan();
    }
}
