package com.hzc.coolcatmusic.ui.chatgpt;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.ui.adapter.BaseRecycleAdapter;
import com.hzc.coolcatmusic.ui.adapter.BaseRecycleViewHolder;

import java.util.List;

public abstract class ChatGPTPopupWindow extends PopupWindow {

    private Context context;
    private List<String> strings;


    public ChatGPTPopupWindow(Context context,List<String> strings) {
        super(context);
        this.context = context;
        this.strings = strings;
        initView();
    }

    private void initView(){
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window_chatgpt,null);
        setContentView(view);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        BaseRecycleAdapter<String> adapter = new BaseRecycleAdapter<String>(context,strings,R.layout.item_popup_window_chatgpt) {
            @Override
            public void convert(BaseRecycleViewHolder holder, String item, int position) {
                TextView tvText = holder.getView(R.id.tv_text);
                View viewLine = holder.getView(R.id.view_line);
                tvText.setText(strings.get(position));
                if(position != strings.size() - 1){
                    viewLine.setVisibility(View.VISIBLE);
                }else{
                    viewLine.setVisibility(View.VISIBLE);
                }
                tvText.setOnClickListener(v -> {
                    onItemClick(ChatGPTPopupWindow.this,strings.get(position));
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);
    }

    public void showAsCenter(View anchor){
        getContentView().measure(
                View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED)
        );
        int popupWidth = getContentView().getMeasuredWidth();
        int popupHeight = getContentView().getMeasuredHeight();
        int x = (anchor.getWidth() - popupWidth)/2;
        int y = - popupHeight - (anchor.getHeight() - popupHeight)/2;
        super.showAsDropDown(anchor, x, y);
    }

    public abstract void onItemClick(PopupWindow window,String string);

}
