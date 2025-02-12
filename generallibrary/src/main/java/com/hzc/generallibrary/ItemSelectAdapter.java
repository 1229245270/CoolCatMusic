package com.hzc.generallibrary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author huangzhichao
 */
public class ItemSelectAdapter extends RecyclerView.Adapter<ItemSelectAdapter.MyViewHolder> {

    private List<ItemBean> list;

    public ItemSelectAdapter(List<ItemBean> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final int pos = position;
        holder.tvName.setText(list.get(position).getText());
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onListener != null){
                    onListener.onDelete(list.get(pos));
                }
            }
        });
    }

    private OnListener onListener;

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    public interface OnListener{
        /**
         * 删除
         * @param  itemBean 选择的项
         */
        void onDelete(ItemBean itemBean);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName;
        private final ImageView ivDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
