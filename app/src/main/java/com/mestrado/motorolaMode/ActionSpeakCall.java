package com.mestrado.motorolaMode;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Rule;

import java.util.Locale;

/**
 * Created by pma035 on 3/2/17.
 */

public class ActionSpeakCall extends Action implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    String phraseToSpeak = "";

    public void setTts(TextToSpeech tts) {
        this.tts = tts;
    }

    public TextToSpeech getTts() {
        return tts;
    }

    @Override
    public void doAction(Rule ruleReceived) {
        //Log.i(MyActivity.LOG, "do action: " + this.getClass());
        tts = new TextToSpeech(ruleReceived.getParentActivity(), this);
        String incomingNumber = ((RuleHome) ruleReceived).getIncomingNumber();

        if (incomingNumber != null && !incomingNumber.isEmpty()) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup
                    .CONTENT_FILTER_URI, Uri.encode(incomingNumber));
            Cursor cursor = ruleReceived.getParentActivity().getContentResolver().query(uri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            AudioManager audioManager = (AudioManager) ruleReceived.getParentActivity()
                    .getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    //audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    1
                    , 0);

            if (cursor != null && cursor.moveToFirst()) {
                phraseToSpeak = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)) + " ";
            } else {
                for (Character c : incomingNumber.toCharArray()) {
                    phraseToSpeak += c + " ";
                }
            }

            cursor.close();
            phraseToSpeak += "is calling";
        } else {
            phraseToSpeak = "Calling number unavailable";
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            int result = tts.setLanguage(Locale.UK);
            //Log.i("TTS", phraseToSpeak);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
            }

            for (int i = 0; i < 20; i++) {
                tts.speak(phraseToSpeak, TextToSpeech.QUEUE_ADD, null);
            }
        } else {
            Log.e("TTS", "Initialization failed!");
        }
    }
}
