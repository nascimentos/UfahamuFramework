package com.mestrado.motorolaMode;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Rule;

/**
 * Created by pma035 on 2/24/17.
 */

public class ActionNormalRingtoneMode extends Action {
    @Override
    public void doAction(Rule ruleReceived) {
        Log.i(MyActivity.LOG, "Action Normal Ringtone");
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(ruleReceived.getParentActivity(),
                RingtoneManager.TYPE_RINGTONE);
        Ringtone ringtone = RingtoneManager.getRingtone(ruleReceived.getParentActivity(), uri);
        AudioManager audioManager = (AudioManager) ruleReceived.getParentActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING,
                //audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                1,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        ringtone.play();
    }
}
