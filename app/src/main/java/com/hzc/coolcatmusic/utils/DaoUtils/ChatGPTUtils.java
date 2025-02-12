package com.hzc.coolcatmusic.utils.DaoUtils;

import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.entity.ChatGPTEntity;
import com.hzc.coolcatmusic.entity.ChatGPTEntityDao;
import com.hzc.coolcatmusic.entity.ChatGPTFormEntity;
import com.hzc.coolcatmusic.entity.ChatGPTFormEntityDao;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.goldze.mvvmhabit.bus.RxBus;

public class ChatGPTUtils {

    /**
     * 根据id获取聊天窗体
     * @param chatForm
     */
    public static ChatGPTFormEntity getChatGPTFormEntityForChatForm(long chatForm){
        if(chatForm == -1){
            return null;
        }
        List<ChatGPTFormEntity> list = AppApplication.daoSession
                .getChatGPTFormEntityDao()
                .queryBuilder()
                .where(ChatGPTFormEntityDao.Properties.Id.eq(chatForm))
                .list();
        if(list != null && list.size() > 0){
            ChatGPTFormEntity chatGPTFormEntity = list.get(0);
            return chatGPTFormEntity;
        }
        return null;
    }

    /**
     * 填加聊天窗体
     * @param chatGPTFormEntity
     */
    public static long addChatGPTFormEntity(ChatGPTFormEntity chatGPTFormEntity){
        return AppApplication.daoSession
                .getChatGPTFormEntityDao()
                .insertOrReplace(chatGPTFormEntity);
    }

    /**
     * 填加聊天数据
     * @param chatGPTEntity
     */
    public static long addChatGPTEntity(ChatGPTEntity chatGPTEntity){
        long id = chatGPTEntity.getChatForm();
        ChatGPTFormEntity chatGPTFormEntity = getChatGPTFormEntityForChatForm(id);
        if(chatGPTFormEntity == null){
            chatGPTFormEntity = new ChatGPTFormEntity();
            chatGPTFormEntity.setCreateDate(System.currentTimeMillis());
            id = addChatGPTFormEntity(chatGPTFormEntity);
        }

        if(id != -1){
            chatGPTEntity.setChatForm(id);
        }
        chatGPTEntity.setCreateDate(System.currentTimeMillis());
        AppApplication.daoSession
                .getChatGPTEntityDao()
                .insertOrReplace(chatGPTEntity);
        chatGPTFormEntity.resetList();

        RxBus.getDefault().post(chatGPTEntity);
        return id;
    }

    /**
     * 根据chatForm获取聊天数据
     * @param chatForm
     */
    public static List<ChatGPTEntity> getChatGPTEntityForChatForm(long chatForm){
        List<ChatGPTEntity> list = AppApplication.daoSession
                .getChatGPTEntityDao()
                .queryBuilder()
                .where(ChatGPTEntityDao.Properties.ChatForm.eq(chatForm))
                .list();
        if(list == null){
            list = new ArrayList<>();
        }
        return Objects.requireNonNull(list);
    }

    /**
     * 获取所有聊天窗体
     */
    public static List<ChatGPTFormEntity> getAllChatGPTForm(){
        List<ChatGPTFormEntity> list = AppApplication.daoSession
                .getChatGPTFormEntityDao()
                .queryBuilder()
                .list();
        if(list == null){
            list = new ArrayList<>();
        }
        return Objects.requireNonNull(list);
    }


}
