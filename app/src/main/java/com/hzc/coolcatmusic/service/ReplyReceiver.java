package com.hzc.coolcatmusic.service;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ReplyReceiver extends BroadcastReceiver {

    public static final String KEY_TEXT_REPLY = "reply";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        String input = bundle.getString(KEY_TEXT_REPLY);
        Uri uri = intent.getData();
        String lastPathSegment = uri.getLastPathSegment();
        long chatId = 0;
        if(lastPathSegment != null){
            chatId = Long.parseLong(lastPathSegment);
        }
        if(chatId > 0 && input != null){

        }
    }


}
