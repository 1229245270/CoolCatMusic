package com.hzc.coolcatmusic.ui.main.look;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hzc.coolcatmusic.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

public class ItemLookVideo extends StandardGSYVideoPlayer {

    private TextView detailBtn;

    ImageView mCoverImage;

    String mCoverOriginUrl;

    int  mCoverOriginId = 0;

    int mDefaultRes;


    public ItemLookVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public ItemLookVideo(Context context) {
        super(context);
    }

    public ItemLookVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        detailBtn = (TextView) findViewById(R.id.detail_btn);
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInPlayingState()) {
                    ItemLookUtil.savePlayState(ItemLookVideo.this);
                    getGSYVideoManager().setLastListener(ItemLookVideo.this);
                    //fixme 页面跳转是，元素共享，效果会有一个中间中间控件的存在
                    //fixme 这时候中间控件 CURRENT_STATE_PLAYING，会触发 startProgressTimer
                    //FIXME 但是没有cancel
                    LookVideoActivity.startTActivity((Activity) getContext(), ItemLookVideo.this);
                }
            }
        });
        if (mIfCurrentIsFullscreen) {
            detailBtn.setVisibility(GONE);
        }
        mCoverImage = findViewById(R.id.thumbImage);

    }

    @Override
    public int getLayoutId() {
        return R.layout.view_look_video;
    }

    public void loadCoverImage(String url, int res) {
        mCoverOriginUrl = url;
        mDefaultRes = res;
        Glide.with(getContext().getApplicationContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                                .error(res)
                                .placeholder(res))
                .load(url)
                .into(mCoverImage);
    }

    public void setSwitchUrl(String url) {
        mUrl = url;
        mOriginUrl = url;
    }

    public void setSwitchCache(boolean cache) {
        mCache = cache;
    }

    public void setSwitchTitle(String title) {
        mTitle = title;
    }

    public void setSurfaceToPlay() {
        addTextureView();
        getGSYVideoManager().setListener(this);
        checkoutState();
    }

    public ItemLookVideo saveState() {
        ItemLookVideo switchVideo = new ItemLookVideo(getContext());
        cloneParams(this, switchVideo);
        return switchVideo;
    }


    public void loadCoverImageBy(int id, int res) {
        mCoverOriginId = id;
        mDefaultRes = res;
        mCoverImage.setImageResource(id);
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        ItemLookVideo sampleCoverVideo = (ItemLookVideo) gsyBaseVideoPlayer;
        if(mCoverOriginUrl != null) {
            sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes);
        } else  if(mCoverOriginId != 0) {
            sampleCoverVideo.loadCoverImageBy(mCoverOriginId, mDefaultRes);
        }
        return gsyBaseVideoPlayer;
    }

    public void cloneState(ItemLookVideo itemLookVideo) {
        cloneParams(itemLookVideo, this);
    }
}
