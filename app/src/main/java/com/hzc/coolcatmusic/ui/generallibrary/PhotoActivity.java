package com.hzc.coolcatmusic.ui.generallibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.databinding.ActivityHomeBinding;
import com.hzc.coolcatmusic.databinding.ActivityPhotoBinding;
import com.hzc.coolcatmusic.ui.homefragment1.LocalMusicViewModel;
import com.hzc.coolcatmusic.ui.main.HomeViewModel;
import com.hzc.generallibrary.util.PhotoUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class PhotoActivity extends BaseActivity<ActivityPhotoBinding, PhotoViewModel>  {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_photo;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public PhotoViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(PhotoViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        TextView textView2 = binding.textView2;
        TextView textView3 = binding.textView3;

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.takePhotoFromCamera(PhotoActivity.this,100,"image");
            }
        });
    }
}
