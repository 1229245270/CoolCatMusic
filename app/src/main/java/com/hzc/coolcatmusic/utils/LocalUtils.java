package com.hzc.coolcatmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.hzc.coolcatmusic.entity.LocalSongEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.utils.KLog;

/**
 * @author hzc
 */
public class LocalUtils {

    private static List<LocalSongEntity> getAllMediaList(Context context, int minDuration, double minSize, int... searchSize) {
        Cursor cursor = null;
        List<LocalSongEntity> mediaList = new ArrayList<LocalSongEntity>();
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
                    null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
            if(cursor == null) {
                KLog.d("The getMediaList cursor is null.");
                return mediaList;
            }
            int count= cursor.getCount();
            if(count <= 0) {
                KLog.d("The getMediaList cursor count is 0.");
            }else{
                //获取本地媒体库音乐数据
                LocalSongEntity mediaEntity = null;
                while (cursor.moveToNext()) {
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    if(artist.equals("<unknown>") || duration <= minDuration * 1000 || size <= minSize * 1024L * 1024L) {
                        continue;
                    }
                    mediaEntity = new LocalSongEntity();
                    mediaEntity.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                    mediaEntity.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                    mediaEntity.setDisplay_name(display_name);
                    mediaEntity.setDuration(duration);
                    mediaEntity.setSize(size);
                    mediaEntity.setArtist(artist);
                    String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    mediaEntity.setPath(data);
                    mediaEntity.setAlbums(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));

                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(data);

                    byte[] cover = mediaMetadataRetriever.getEmbeddedPicture();
                    if(cover != null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(cover,0,cover.length);
                        mediaEntity.setImage(FileUtil.getDownloadFile(bitmap,display_name));
                    }
                    mediaList.add(mediaEntity);

                    if(searchSize != null && searchSize.length >= 1 && mediaList.size() >= searchSize[0]){
                        return mediaList;
                    }
                }
            }
            //获取应用缓存音乐数据
            String destFileDir = context.getCacheDir().getPath();
            File destFile = new File(destFileDir);
            File[] files = destFile.listFiles();
            if(files != null && files.length > 0){
                LocalSongEntity mediaEntity = null;
                for(File file : files){
                    if(file.isFile()){
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        String fileName = file.getName();
                        String path = file.getPath();
                        mmr.setDataSource(path);
                        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        // 播放时长单位为毫秒
                        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        // 图片，可以通过BitmapFactory.decodeByteArray转换为bitmap图片
                        byte[] pic = mmr.getEmbeddedPicture();

                        mediaEntity = new LocalSongEntity();
                        mediaEntity.setDisplay_name(fileName);
                        mediaEntity.setDuration(Integer.parseInt(duration));
                        mediaEntity.setPath(path);
                        if(album == null || artist == null){
                            try {
                                album = fileName.substring(fileName.indexOf(" - ") + 3,fileName.lastIndexOf(".mp3"));
                                artist = fileName.substring(0,fileName.indexOf(" - "));
                            }catch (Exception e){
                                KLog.e(e.toString());
                            }
                        }
                        mediaEntity.setArtist(artist);
                        mediaEntity.setAlbums(album);
                        mediaEntity.setTitle(title);
                        if(pic != null){
                            Bitmap bitmap = BitmapFactory.decodeByteArray(pic,0,pic.length);
                            mediaEntity.setImage(FileUtil.getDownloadFile(bitmap,path));
                        }
                        mediaList.add(mediaEntity);

                        if(searchSize != null && searchSize.length >= 1 && mediaList.size() >= searchSize[0]){
                            return mediaList;
                        }
                    }
                }
            }
        } catch (Exception e) {
            KLog.e(e.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return mediaList;
    }

    /**
     * 获取本地歌曲
     * @param context 上下文
     * @param minDuration 时长限制
     * @param minSize 大小限制
     * @param searchSize 歌曲数
     * @return 本地歌曲
     */
    public static Observable<List<LocalSongEntity>> getLocalMusicObservable(Context context,int minDuration, double minSize, int... searchSize){
        return Observable.create(new ObservableOnSubscribe<List<LocalSongEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<LocalSongEntity>> emitter) throws Exception {
                List<LocalSongEntity> list = new ArrayList<>(getAllMediaList(context, minDuration, minSize,searchSize));
                emitter.onNext(list);
            }
        }).map((Function<List<LocalSongEntity>, List<LocalSongEntity>>) objects -> objects);
    }
}
