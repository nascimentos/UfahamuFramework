package com.mestrado.motorolaMode;

import android.media.AudioManager;
import android.util.Log;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Rule;

/**
 * Created by pma035 on 2/24/17.
 */

public class ActionNormalMode extends Action {
    @Override
    public void doAction(Rule ruleReceived) {
        Log.i(MyActivity.LOG, "Action Normal");
        ((AudioManager) ruleReceived.getParentActivity()
                .getSystemService(android.content.Context.AUDIO_SERVICE))
                .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
}
