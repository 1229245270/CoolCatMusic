package com.hzc.generallibrary;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangzhichao
 */
public abstract class ItemBeanAdapter extends RecyclerView.Adapter<ItemBeanAdapter.MyViewHolder> {

    private List<ItemBean> list;
    private List<ItemBean> selectList = new ArrayList<>();

    private int selectColor = Color.parseColor("#ffffff");
    /**
     * 未选中的颜色
     */
    private int unSelectColor = Color.parseColor("#fafafa");

    private boolean isRadio = true;

    private String type = "";

    public ItemBeanAdapter(List<ItemBean> list){
        this.list = list;
    }

    public void setSelectList(List<ItemBean> selectList) {
        for(ItemBean select : selectList){
            for(int i = 0;i < list.size();i++){
                if(list.get(i).getId().equals(select.getId())){
                    list.get(i).setSelect(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }

    public void setUnSelectColor(int unSelectColor) {
        this.unSelectColor = unSelectColor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bean,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final int pos = position;
        holder.tvName.setText(list.get(position).getText());
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onListener != null){
                    onListener.onClick(list.get(pos), pos);
                }
            }
        });

        onBindViewHolder(list.get(position).isSelect(),holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 选中变化
     * @param isSelect 是否选中
     * @param holder 选项
     */
    public abstract void onBindViewHolder(boolean isSelect,final MyViewHolder holder);

    private ItemBeanAdapter.OnListener onListener;

    public void setOnListener(ItemBeanAdapter.OnListener onListener) {
        this.onListener = onListener;
    }

    public interface OnListener{
        void onClick(ItemBean itemBean,int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public final TextView tvName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            //ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //tvName.setLayoutParams(layoutParams);
            //tvName.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
