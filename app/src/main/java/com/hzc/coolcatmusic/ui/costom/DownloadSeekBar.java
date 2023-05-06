package com.hzc.coolcatmusic.ui.costom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hzc.coolcatmusic.R;

/**
 * @author 12292
 */
public class DownloadSeekBar extends LinearLayout {
    public DownloadSeekBar(Context context) {
        this(context,null);
    }

    public DownloadSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DownloadSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private TextView textView;
    private ProgressBar progressBar;

    private void initView(){
        View view = View.inflate(getContext(),R.layout.seekbar_download,null);
        textView = view.findViewById(R.id.textView);
        progressBar = view.findViewById(R.id.progressBar);
        addView(view);
    }

    public TextView getTextView() {
        return textView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
