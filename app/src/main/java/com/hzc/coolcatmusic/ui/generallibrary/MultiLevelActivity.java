package com.hzc.coolcatmusic.ui.generallibrary;

import com.hzc.generallibrary.BaseMultiLevelSelectionActivity;
import com.hzc.generallibrary.ItemBean;

import java.util.ArrayList;
import java.util.List;

public class MultiLevelActivity extends BaseMultiLevelSelectionActivity{

    @Override
    public void onClickLeftItem(ItemBean itemBean, int position) {
        add(rightTopList,position + "--",10);
        setRightTopList(rightTopList,0);
    }

    @Override
    public void onClickRightTopItem(ItemBean itemBean, int position) {
        add(rightList,position + "--",10);
        setRightList(rightList);
    }

    @Override
    public void onClickRightItem(ItemBean itemBean, int position) {

    }

    @Override
    public void onClickSearchItem(ItemBean itemBean, int position) {

    }

    private List<ItemBean> leftList;
    private List<ItemBean> rightTopList;
    private List<ItemBean> rightList;

    private void add(List<ItemBean> list,String s,int n){
        list.clear();
        for(int i = 0;i < n;i++){
            ItemBean itemBean = new ItemBean();
            itemBean.setText(s + i);
            itemBean.setId("" + i);
            list.add(itemBean);
        }
    }

    @Override
    public void initData() {
        leftList = new ArrayList<>();
        rightTopList = new ArrayList<>();
        rightList = new ArrayList<>();
        add(leftList,"item",10);

        setLeftList(leftList,0);

        //setRightTopList(leftList,0);
        //setRightList(leftList);
        //setSearchList();
    }
}
