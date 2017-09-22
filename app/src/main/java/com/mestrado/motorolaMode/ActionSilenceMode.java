package com.mestrado.motorolaMode;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Rule;

/**
 * Created by pma035 on 2/21/17.
 */

public class ActionSilenceMode extends Action {
    @Override
    public void doAction(Rule ruleReceived) {
        Log.i(MyActivity.LOG, "Action Silence");
        AudioManager audioManager = (AudioManager) ruleReceived.getParentActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
}
