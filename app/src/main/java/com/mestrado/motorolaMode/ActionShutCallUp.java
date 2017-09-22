package com.mestrado.motorolaMode;

import android.speech.tts.TextToSpeech;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Rule;

/**
 * Created by pma035 on 3/3/17.
 */

public class ActionShutCallUp extends Action {
    private Action actionSpeakOut;

    public ActionShutCallUp(Action actionSpeakOut) {
        this.actionSpeakOut = actionSpeakOut;
    }

    @Override
    public void doAction(Rule ruleReceived) {
        TextToSpeech tts = ((ActionSpeakCall) actionSpeakOut).getTts();

        if (tts != null) {
            //Log.i("TTS", "tts stoped");
            tts.stop();
            tts.shutdown();
            ((ActionSpeakCall) actionSpeakOut).setTts(null);
        }
    }
}
