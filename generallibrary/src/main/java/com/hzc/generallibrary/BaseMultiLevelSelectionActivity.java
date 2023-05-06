package com.hzc.generallibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangzhichao
 */
public abstract class BaseMultiLevelSelectionActivity extends AppCompatActivity {

    private LinearLayout llContent,llSearch,llSelected;
    private EditText etSearch;
    private ImageView ivSearch;
    private TextView tvSelected,tvNoData;
    private RecyclerView rvSelect,rvLeftList,rvRightTopList,rvRightList,rvSearch;

    private List<ItemBean> leftList;
    private List<ItemBean> rightTopList;
    private List<ItemBean> rightList;
    private List<ItemBean> searchList;
    private ItemBeanAdapter leftAdapter;
    private ItemBeanAdapter rightAdapter;
    private ItemBeanAdapter rightTopAdapter;
    private ItemBeanAdapter searchAdapter;

    private ItemBean selectLeftItem;
    private int selectLeftPosition;
    private ItemBean selectRightTopItem;
    private int selectRightTopPosition;
    private List<ItemBean> selectItem;
    private ItemSelectAdapter selectAdapter;
    private boolean isRadio = true;

    public void setRadio(boolean radio) {
        isRadio = radio;
    }

    public boolean isRadio() {
        return isRadio;
    }

    /**
     * 获取当前左侧选择的item
     * @return
     */
    public ItemBean getSelectLeftItem(){
        return selectLeftItem;
    }

    /**
     * 获取当前左侧选择的位置
     * @return
     */
    public int getSelectLeftPosition() {
        return selectLeftPosition;
    }

    /**
     * 获取当前右上侧选择的item
     * @return
     */
    public ItemBean getSelectRightTopItem(){
        return selectRightTopItem;
    }

    /**
     * 获取当前右上侧选择的位置
     * @return
     */
    public int getSelectRightTopPosition() {
        return selectRightTopPosition;
    }

    /**
     * 左侧列表点击
     * @param itemBean 子项
     * @param position 位置
     */
    public abstract void onClickLeftItem(ItemBean itemBean, int position);

    /**
     * 右上侧列表点击
     * @param itemBean 子项
     * @param position 位置
     */
    public abstract void onClickRightTopItem(ItemBean itemBean, int position);

    /**
     * 右侧列表点击
     * @param itemBean 子项
     * @param position 位置
     */
    public abstract void onClickRightItem(ItemBean itemBean,int position);

    /**
     * 搜索列表点击
     * @param itemBean 子项
     * @param position 位置
     */
    public abstract void onClickSearchItem(ItemBean itemBean,int position);

    /**
     * 初始化数据
     */
    public abstract void initData();

    private int backgroundColor = Color.parseColor("#f5f5f5");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_level_selection);
        llContent = findViewById(R.id.ll_content);
        llSearch = findViewById(R.id.ll_search);
        llSelected = findViewById(R.id.ll_selected);
        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.iv_search);
        tvSelected = findViewById(R.id.tv_selected);
        tvNoData = findViewById(R.id.tv_no_data);
        rvSearch = findViewById(R.id.rv_search);
        rvLeftList = findViewById(R.id.rv_left_list);
        rvRightTopList = findViewById(R.id.rv_right_top_list);
        rvRightList = findViewById(R.id.rv_right_list);
        rvSelect = findViewById(R.id.rv_select);

        leftList = new ArrayList<>();
        rightTopList = new ArrayList<>();
        rightList = new ArrayList<>();
        searchList = new ArrayList<>();
        selectItem = new ArrayList<>();

        llContent.setBackgroundColor(backgroundColor);

        leftAdapter = new ItemBeanAdapter(leftList) {
            @Override
            public void onBindViewHolder(boolean isSelect, MyViewHolder holder) {
                if(isSelect){
                    holder.tvName.setTextColor(Color.RED);
                }else{
                    holder.tvName.setTextColor(Color.BLACK);
                }
            }
        };
        leftAdapter.setOnListener(new ItemBeanAdapter.OnListener() {
            @Override
            public void onClick(ItemBean itemBean, int position) {
                checkLeftItem(itemBean, position);
            }
        });
        leftAdapter.setSelectColor(Color.parseColor("#ffffff"));
        leftAdapter.setUnSelectColor(backgroundColor);
        rvLeftList.setBackgroundColor(Color.parseColor("#dfdfdf"));
        rvLeftList.setLayoutManager(new LinearLayoutManager(this));
        rvLeftList.setAdapter(leftAdapter);

        rightAdapter = new ItemBeanAdapter(rightList) {
            @Override
            public void onBindViewHolder(boolean isSelect, MyViewHolder holder) {

            }
        };
        rightAdapter.setOnListener(new ItemBeanAdapter.OnListener() {
            @Override
            public void onClick(ItemBean itemBean, int position) {
                updateSelectItem(true,itemBean);
                onClickRightItem(itemBean,position);
            }
        });
        rightAdapter.setSelectColor(Color.parseColor("#ffffff"));
        rightAdapter.setUnSelectColor(Color.parseColor("#ffffff"));
        rvRightList.setBackgroundColor(Color.parseColor("#ffffff"));
        rvRightList.setLayoutManager(new LinearLayoutManager(this));
        rvRightList.setAdapter(rightAdapter);

        rightTopAdapter = new ItemBeanAdapter(rightTopList) {
            @Override
            public void onBindViewHolder(boolean isSelect, MyViewHolder holder) {

            }
        };
        rightTopAdapter.setOnListener(new ItemBeanAdapter.OnListener() {
            @Override
            public void onClick(ItemBean itemBean, int position) {
                checkRightTopItem(itemBean,position);
            }
        });
        rightTopAdapter.setSelectColor(Color.parseColor("#ffffff"));
        rightTopAdapter.setUnSelectColor(Color.parseColor("#f5f5f5"));
        rvRightTopList.setBackgroundColor(Color.parseColor("#f5f5f5"));
        LinearLayoutManager managerHorizontal = new LinearLayoutManager(this);
        managerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvRightTopList.setLayoutManager(managerHorizontal);
        rvRightTopList.setAdapter(rightTopAdapter);

        searchAdapter = new ItemBeanAdapter(searchList) {
            @Override
            public void onBindViewHolder(boolean isSelect, MyViewHolder holder) {

            }
        };
        searchAdapter.setOnListener(new ItemBeanAdapter.OnListener() {
            @Override
            public void onClick(ItemBean itemBean, int position) {
                onClickSearchItem(itemBean,position);
            }
        });
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setAdapter(searchAdapter);

        selectAdapter = new ItemSelectAdapter(selectItem);
        selectAdapter.setOnListener(new ItemSelectAdapter.OnListener() {
            @Override
            public void onDelete(ItemBean itemBean) {
                updateSelectItem(false,itemBean);
            }
        });
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rvSelect.setLayoutManager(flexboxLayoutManager);
        rvSelect.setAdapter(selectAdapter);
        llSearch.setVisibility(View.VISIBLE);

        initData();
    }

    /**
     * 初始化数据，供数据显示
     * 设置左侧数据和左侧选中项
     * @param leftList 左侧数据
     * @param item 选中的左侧item
     */
    public void setLeftList(List<ItemBean> leftList,Object item) {
        this.leftList.clear();
        this.leftList.addAll(leftList);
        leftAdapter.notifyDataSetChanged();
        this.rightTopList.clear();
        rightTopAdapter.notifyDataSetChanged();
        if(leftList.size() > 0){
            showSelectItemList(true);
            tvNoData.setVisibility(View.GONE);
        }else{
            showSelectItemList(false);
            tvNoData.setVisibility(View.VISIBLE);
        }

        if(item instanceof Integer){
            int position = (int) item;
            if(leftList.size() > position){
                checkLeftItem(leftList.get(position),position);
            }
        }else if(item instanceof ItemBean){
            ItemBean itemBean = (ItemBean) item;
            for(int i = 0;i < leftList.size();i++){
                if(itemBean.getId().equals(leftList.get(i).getId())){
                    checkLeftItem(itemBean,i);
                    break;
                }
            }
        }
    }

    /**
     * 设置右上侧数据和右上侧选中项
     * @param rightTopList 右上侧数据
     * @param item 选中的右上侧item
     */
    public void setRightTopList(List<ItemBean> rightTopList,Object item) {
        this.rightTopList.clear();
        this.rightTopList.addAll(rightTopList);
        rightTopAdapter.notifyDataSetChanged();
        this.rightList.clear();
        rightAdapter.notifyDataSetChanged();

        if(item instanceof Integer){
            int position = (int) item;
            if(rightTopList.size() > position){
                checkRightTopItem(rightTopList.get(position),position);
            }
        }else if(item instanceof ItemBean){
            ItemBean itemBean = (ItemBean) item;
            for(int i = 0;i < rightTopList.size();i++){
                if(itemBean.getId().equals(rightTopList.get(i).getId())){
                    checkRightTopItem(rightTopList.get(i),i);
                    break;
                }
            }
        }
    }

    /**
     * 选中右上侧选项
     * @param itemBean 选项
     * @param position 位置
     */
    private void checkRightTopItem(ItemBean itemBean,int position){
        selectRightTopItem = itemBean;
        selectRightTopPosition = position;
        for(int i = 0;i < rightTopList.size();i++){
            rightTopList.get(i).setSelect(false);
        }
        rightTopList.get(position).setSelect(true);
        rightTopAdapter.notifyDataSetChanged();
        onClickRightTopItem(itemBean,position);
    }

    /**
     * 选中左侧选项
     * @param itemBean 选项
     * @param position 位置
     */
    private void checkLeftItem(ItemBean itemBean,int position){
        selectLeftItem = itemBean;
        selectLeftPosition = position;
        for(int i = 0;i < leftList.size();i++){
            leftList.get(i).setSelect(false);
        }
        leftList.get(position).setSelect(true);
        leftAdapter.notifyDataSetChanged();
        onClickLeftItem(itemBean,position);
    }

    /**
     * 设置右侧数据
     * @param rightList 右侧数据
     */
    public void setRightList(List<ItemBean> rightList) {
        this.rightList.clear();
        this.rightList.addAll(rightList);
        rightAdapter.notifyDataSetChanged();
    }

    /**
     * 设置搜索列表数据
     * @param list 搜索数据
     */
    public void setSearchList(List<ItemBean> list){
        searchList.clear();
        searchList.addAll(list);
        searchAdapter.notifyDataSetChanged();
        showSelectItemList(false);
        if(list.size() > 0){
            tvNoData.setVisibility(View.GONE);
            rvSearch.setVisibility(View.VISIBLE);
        }else{
            tvNoData.setVisibility(View.VISIBLE);
            rvSearch.setVisibility(View.GONE);
        }
    }

    private void showSelectItemList(boolean bool){
        if(bool){
            rvLeftList.setVisibility(View.VISIBLE);
            rvRightTopList.setVisibility(View.VISIBLE);
            rvRightList.setVisibility(View.VISIBLE);
        }else{
            rvLeftList.setVisibility(View.GONE);
            rvRightTopList.setVisibility(View.GONE);
            rvRightList.setVisibility(View.GONE);
        }
    }

    /**
     * 添加/删除选择的数据
     * @param item
     */
    public void updateSelectItem(boolean isAdd,ItemBean item){
        if(isAdd){
            if(!selectItem.contains(item)){
                selectItem.add(item);
            }
        }else{
            selectItem.remove(item);
        }
        if(selectItem.size() > 0){
            llSelected.setVisibility(View.VISIBLE);
        }else{
            llSelected.setVisibility(View.GONE);
        }
        selectAdapter.notifyDataSetChanged();
    }

}
