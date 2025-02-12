package com.hzc.generallibrary;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author huangzhichao
 */
public class ItemHorizontalAdapter extends RecyclerView.Adapter<ItemHorizontalAdapter.MyViewHolder> {

    private List<ItemBean> list;
    private int selectColor = Color.parseColor("#ffffff");
    private int unSelectColor = Color.parseColor("#fafafa");
    private boolean isRadio = true;

    public ItemHorizontalAdapter(List<ItemBean> list){
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
                    onListener.onClick(list.get(pos),pos);
                }
            }
        });
        if(list.get(position).isSelect()){
            holder.itemView.setBackgroundColor(selectColor);
        }else{
            holder.itemView.setBackgroundColor(unSelectColor);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private OnListener onListener;

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    public interface OnListener{
        void onClick(ItemBean itemBean,int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
