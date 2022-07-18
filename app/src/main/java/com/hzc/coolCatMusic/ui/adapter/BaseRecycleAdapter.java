package com.hzc.coolCatMusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolCatMusic.R;

import java.util.List;

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseRecycleViewHolder> {
    private Context context;
    private List<T> mData;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;
    private int viewId;

    public BaseRecycleAdapter(Context context, List<T> mData,int viewId){
        this.context = context;
        this.mData = mData;
        this.viewId = viewId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener{
        void onItemClick(Object object,int position);
        void onItemLongClick(Object object,int position);
    }

    @NonNull
    @Override
    public BaseRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseRecycleViewHolder.getRecycleViewHolder(context,inflater.inflate(viewId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecycleViewHolder holder, int position) {
        convert(holder, mData.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(mData.get(holder.getAdapterPosition()),holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onItemLongClick(mData.get(holder.getAdapterPosition()),holder.getAdapterPosition());
                return false;
            }
        });
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract void convert(BaseRecycleViewHolder holder,T item,int position);

}
