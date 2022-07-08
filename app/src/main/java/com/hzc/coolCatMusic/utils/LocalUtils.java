package com.hzc.coolCatMusic.utils;

import static com.hzc.coolCatMusic.utils.TagUtils.LocalUtilsTAG;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.hzc.coolCatMusic.entity.LocalSongEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

public class LocalUtils {

    public static List<LocalSongEntity> getAllMediaList(Context context, int minDuration,int minSize) {
        Cursor cursor = null;
        List<LocalSongEntity> mediaList = new ArrayList<LocalSongEntity>();
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
                    null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
            if(cursor == null) {
                KLog.d(LocalUtilsTAG, "The getMediaList cursor is null.");
                return mediaList;
            }
            int count= cursor.getCount();
            if(count <= 0) {
                KLog.d(LocalUtilsTAG, "The getMediaList cursor count is 0.");
                return mediaList;
            }
            mediaList = new ArrayList<LocalSongEntity>();
            LocalSongEntity mediaEntity = null;
            while (cursor.moveToNext()) {
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                if(artist.equals("<unknown>") || duration <= minDuration * 1000 || size <= minSize * 1024L * 1024L) {
                    continue;
                }
                mediaEntity = new LocalSongEntity();
                mediaEntity.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                mediaEntity.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                mediaEntity.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                mediaEntity.setDuration(duration);
                mediaEntity.setSize(size);
                mediaEntity.setArtist(artist);
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                mediaEntity.setPath(data);
                mediaEntity.setAlbums(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(data);

                byte[] cover = mediaMetadataRetriever.getEmbeddedPicture();
                if(cover != null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(cover,0,cover.length);
                    mediaEntity.setImage(bitmap);
                }
                KLog.d(LocalUtilsTAG,mediaEntity.toString());
                mediaList.add(mediaEntity);
            }
        } catch (Exception e) {
            KLog.d(LocalUtilsTAG,e.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return mediaList;
    }

}
