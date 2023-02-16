package com.hzc.coolcatmusic.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzc.coolcatmusic.R;

public class BaseRecycleViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view ,int position);
        void onItemLongClick(View view ,int position);
    }

    public BaseRecycleViewHolder(Context context,@NonNull View itemView) {
        super(itemView);
        this.context = context;
        views = new SparseArray<>();
    }

    public static BaseRecycleViewHolder getRecycleViewHolder(Context context,View itemView){
        return new BaseRecycleViewHolder(context,itemView);
    }

    public SparseArray<View> getViews(){
        return views;
    }

    public <T extends View> T getView(int viewId){
        View view = views.get(viewId);
        if(view == null){
            view = itemView.findViewById(viewId);
            views.put(viewId,view);
        }
        return (T) view;
    }

    public BaseRecycleViewHolder setText(int viewId,String text){
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public BaseRecycleViewHolder setBackground(int viewId, Drawable drawable){
        View view = getView(viewId);
        view.setBackground(drawable);
        return this;
    }

    public BaseRecycleViewHolder setTextColor(int viewId, int color){
        TextView textView = getView(viewId);
        textView.setTextColor(color);
        return this;
    }

    public BaseRecycleViewHolder setImageResource(int viewId,Object drawableId){
        ImageView imageView = getView(viewId);
        if(drawableId != null){
            if(drawableId instanceof String && drawableId != ""){
                Glide.with(context).load((String)drawableId).error(R.mipmap.ic_launcher).into(imageView);
            }else if(drawableId instanceof Integer){
                imageView.setImageResource((Integer) drawableId);
            }else{
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }else{
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
        return this;
    }

    /*public BaseRecycleViewHolder setImageNumber(int viewId,int num){
        //NumberImageView numberImageView = getView(viewId);
        //numberImageView.setNum(num);
        return this;
    }
    public RecyclerView getRecycleView(int viewId){
        RecyclerView recyclerView = getView(viewId);
        return recyclerView;
    }
    public Button getButton(int viewId){
        Button button = getView(viewId);
        return button;
    }
    public BaseRecycleViewHolder setVisibility(int viewId,boolean visibility){
        View view = getView(viewId);
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);
        return this;
    }
    public BaseRecycleViewHolder setSelect(int showView,int hideView,boolean boo){
        if(hideView != 0){
            View hide =  getView(hideView);
            hide.setVisibility(boo ? View.VISIBLE : View.GONE);
        }
        if(showView != 0){
            View show = getView(showView);
            show.setVisibility(boo ? View.GONE : View.VISIBLE);
        }
        return this;
    }
    public BaseRecycleViewHolder setSelectColor(int tv,boolean boo,String newColor,String oldColor){
        TextView textView = getView(tv);
        if(boo){
            textView.setTextColor(Color.parseColor(newColor));
        }else{
            textView.setTextColor(Color.parseColor(oldColor));
        }
        return this;
    }

    public BaseRecycleViewHolder setHeightAndWidth(Activity activity, int id){
        CircularImageView circularImageView = getView(id);
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) circularImageView.getLayoutParams();
        layoutParams.height = display.getWidth()/2 - 40;
        layoutParams.width = display.getWidth()/2 - 40;
        circularImageView.setLayoutParams(layoutParams);
        return this;
    }

    public BaseRecycleViewHolder setTextSize(int tv,int textSize){
        TextView textView = getView(tv);
        textView.setTextSize(textSize);
        return this;
    }
    public BaseRecycleViewHolder setVideoURL(Context context,int videoView,String url,String title){
        JzvdStd jzvdStd = getView(videoView);
        jzvdStd.setUp(url,title);
        return this;
    }*/
    /*
    public BaseRecycleViewHolder setVideoPlay(int videoView,int play){
        if(getView(videoView) instanceof SuperPlayerView) {
            SuperPlayerView superPlayerView = getView(videoView);
            if(play == SUPERVIEW_RELEASE){
                superPlayerView.release();
                if (superPlayerView.getPlayMode() != SuperPlayerConst.PLAYMODE_FLOAT){
                    superPlayerView.resetPlayer();
                }
            }else if(play == SUPERVIEW_PAUSE){
                if (superPlayerView.getPlayMode() != SuperPlayerConst.PLAYMODE_FLOAT) {
                    superPlayerView.onPause();
                }
            }
        }
        return this;
    }*/
}
