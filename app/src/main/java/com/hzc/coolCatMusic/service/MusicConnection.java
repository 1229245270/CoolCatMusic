package com.hzc.coolCatMusic.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MusicConnection implements ServiceConnection {

    public static MusicService.MusicInterface musicInterface;


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        musicInterface = (MusicService.MusicInterface) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicInterface = null;
    }
}
