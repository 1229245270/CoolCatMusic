package com.hzc.coolCatMusic.ui.costom;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolCatMusic.ui.adapter.BaseRecycleAdapter;
import com.hzc.coolCatMusic.ui.adapter.SongAdapter;
import com.hzc.coolCatMusic.ui.main.HomeFragment1ViewModel;

import java.util.Collections;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

public abstract class RecycleViewTouchHelper<T> extends ItemTouchHelper.Callback {
    private final List<T> list;
    private final BaseRecycleAdapter<T> adapter;
    private boolean edit = false;

    public RecycleViewTouchHelper(List<T> list, BaseRecycleAdapter<T> adapter){
        this.list = list;
        this.adapter = adapter;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if(!edit){
            return 0;
        }
        if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags,swipeFlags);
        }else{
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags,swipeFlags);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        KLog.d("fromPosition:" + fromPosition + "," + toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    //长按选中
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null && actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

            viewHolder.itemView.setBackgroundColor(Color.GRAY);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //长按松开
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
        edit = false;
        moveResult();
    }

    public abstract void moveResult();

    //重写拖拽不可用
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
