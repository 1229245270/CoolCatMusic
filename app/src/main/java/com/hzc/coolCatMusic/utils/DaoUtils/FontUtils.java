package com.hzc.coolCatMusic.utils.DaoUtils;

import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.entity.Font;
import com.hzc.coolCatMusic.entity.FontDao;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntityDao;

import java.util.List;

public class FontUtils {

    public static void updateFontEntity(Font font){
        AppApplication.daoSession
                .getFontDao()
                .insertOrReplace(font);
    }

    public static Font getFontEntity(Long id){
        List<Font> list = AppApplication.daoSession
                .getFontDao()
                .queryBuilder()
                .where(FontDao.Properties.Id.eq(id))
                .list();
        if(list != null && list.size() > 0) return list.get(0);
        return null;
    }
}
