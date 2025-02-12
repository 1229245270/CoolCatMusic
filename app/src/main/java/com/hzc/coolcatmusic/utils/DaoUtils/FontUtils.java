package com.hzc.coolcatmusic.utils.DaoUtils;

import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.entity.Font;
import com.hzc.coolcatmusic.entity.FontDao;

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
