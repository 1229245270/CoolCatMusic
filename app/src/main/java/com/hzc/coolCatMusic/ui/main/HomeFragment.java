package com.hzc.coolCatMusic.ui.main;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentHomeBinding;
import com.hzc.coolCatMusic.ui.adapter.HomeCollectionAdapter;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.lang.reflect.Field;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.KLog;

public class HomeFragment extends BaseFragment<FragmentHomeBinding,HomeFragmentViewModel> implements SlidingPaneLayout.PanelSlideListener {

    public static HomeFragment instance;

    public static HomeFragment getInstance(){
        if(instance == null){
            instance = new HomeFragment();
        }
        return instance;
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public HomeFragmentViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(HomeFragmentViewModel.class);
    }

    public BoomMenuButton mainRightMenu;
    public ViewPager2 mainViewPager;
    public TabLayout mainTab;

    @Override
    public void initData() {
        super.initData();
        initMainRightMenu();
        initMainTab();

        //initSwipeBackFinish();
    }

    private void initMainRightMenu(){
        mainRightMenu = binding.mainRightMenu;
        mainRightMenu.setBackgroundEffect(false);
        mainRightMenu.setShadowEffect(false);
        mainRightMenu.setButtonRadius(50);
        mainRightMenu.setButtonEnum(ButtonEnum.Ham);
        mainRightMenu.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        mainRightMenu.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);

        for (int i = 0; i < mainRightMenu.getPiecePlaceEnum().pieceNumber(); i++) {
            HamButton.Builder builder;
            Rect rect = new Rect(20,20,20,20);
            switch (i){
                case 0:
                    builder = new HamButton.Builder()
                            .normalImageRes(R.drawable.home_music_recognition)
                            .imagePadding(rect)
                            .normalColor(Color.WHITE)
                            .normalText("听歌识曲")
                            .subNormalText("Little")
                            .normalTextColor(Color.BLACK)
                            .subNormalTextColor(Color.BLACK);
                    break;
                case 1:
                    builder = new HamButton.Builder()
                            .normalImageRes(R.drawable.home_scan)
                            .imagePadding(rect)
                            .normalColor(Color.WHITE)
                            .normalText("扫一扫")
                            .subNormalText("Little")
                            .normalTextColor(Color.BLACK)
                            .subNormalTextColor(Color.BLACK);
                    break;
                case 2:
                    builder = new HamButton.Builder()
                            .normalImageRes(R.drawable.home_switch_theme)
                            .imagePadding(rect)
                            .normalColor(Color.WHITE)
                            .normalText("主题切换")
                            .subNormalText("Little")
                            .normalTextColor(Color.BLACK)
                            .subNormalTextColor(Color.BLACK);
                    break;
                case 3:
                    builder = new HamButton.Builder()
                            .normalImageRes(R.drawable.home_share)
                            .imagePadding(rect)
                            .normalColor(Color.WHITE)
                            .normalText("音乐分享")
                            .subNormalText("Little")
                            .normalTextColor(Color.BLACK)
                            .subNormalTextColor(Color.BLACK);
                    break;
                default:
                    builder = new HamButton.Builder()
                            .normalImageRes(R.mipmap.ic_launcher)
                            .normalColor(Color.WHITE)
                            .normalText("normalText")
                            .subNormalText("subNormalText")
                            .normalTextColor(Color.BLACK)
                            .subNormalTextColor(Color.BLACK);
            }
            mainRightMenu.addBuilder(builder);
        }
    }

    private void initMainTab(){
        mainViewPager = binding.mainViewPager;
        mainTab = binding.mainTab;
        HomeCollectionAdapter adapter = new HomeCollectionAdapter(this);
        mainViewPager.setAdapter(adapter);
        new TabLayoutMediator(mainTab,mainViewPager,
                (tab,position) -> {
                    switch (position){
                        case 0:
                            tab.setText("听");
                            break;
                        case 1:
                            tab.setText("看");
                            break;
                        case 2:
                            tab.setText("写");
                            break;
                        default:
                            tab.setText("Tab " + position);
                    }
                }
        ).attach();
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.isOpenDrawerLayout.observe(this, aBoolean -> {

            //mainDrawerLayout.openDrawer(GravityCompat.START);
        });
    }

    @Override
    public void onPanelSlide(@NonNull View panel, float slideOffset) {

    }

    @Override
    public void onPanelOpened(@NonNull View panel) {
        super.onDestroy();
        //getActivity().overridePendingTransition(0,R.anim.back_to_right);
        //KLog.d("onPanelOpened");
    }

    @Override
    public void onPanelClosed(@NonNull View panel) {

    }

    protected boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 初始化滑动返回
     */
    /*private void initSwipeBackFinish() {
        if (isSupportSwipeBack()) {
            SlidingPaneLayout slidingPaneLayout = new SlidingPaneLayout();
            //通过反射改变mOverhangSize的值为0，这个mOverhangSize值为菜单到右边屏幕的最短距离，默认
            //是32dp，现在给它改成0
            try {
                //mOverhangSize属性，意思就是左菜单离右边屏幕边缘的距离
                Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
                f_overHang.setAccessible(true);
                //设置左菜单离右边屏幕边缘的距离为0，设置全屏
                f_overHang.set(slidingPaneLayout, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            slidingPaneLayout.setPanelSlideListener(this);
            slidingPaneLayout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
            // 左侧的透明视图
            View leftView = new View(getContext());
            leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            leftView.setBackgroundColor(Color.TRANSPARENT);
            slidingPaneLayout.addView(leftView, 0);  //添加到SlidingPaneLayout中
            // 右侧的内容视图
            ViewGroup decor = (ViewGroup) getActivity().getWindow().getDecorView();
            ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
            decorChild.setBackgroundColor(getResources().getColor(android.R.color.white));
            decor.removeView(decorChild);
            decor.addView(slidingPaneLayout);
            // 为 SlidingPaneLayout 添加内容视图
            slidingPaneLayout.addView(decorChild, 1);
        }
    }*/
}
