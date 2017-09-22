package com.mestrado.motorolaMode;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Rule;

import java.util.Locale;

/**
 * Created by pma035 on 3/7/17.
 */

public class ActionSpeakMessage extends Action implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    String phraseToSpeak = "";

    public TextToSpeech getTts() {
        return tts;
    }

    @Override
    public void doAction(Rule ruleReceived) {
        Log.i(MyActivity.LOG, "Action speak message");
        tts = new TextToSpeech(ruleReceived.getParentActivity(), this);
        Bundle bundle = ((NewContextIncomingMessage) ruleReceived.getContexts().get(0)).getBundleIncomingMessage();

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);
            String smsContent = sms.getMessageBody();
            String smsAddress = sms.getOriginatingAddress();
            phraseToSpeak = "Message from ";

            if (smsAddress != null && !smsAddress.isEmpty()) {
                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup
                        .CONTENT_FILTER_URI, Uri.encode(smsAddress));
                Cursor cursor = ruleReceived.getParentActivity().getContentResolver().query(uri,
                        new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
                AudioManager audioManager = (AudioManager) ruleReceived.getParentActivity()
                        .getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        //audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                        2
                        , 0);

                if (cursor != null && cursor.moveToFirst()) {
                    phraseToSpeak = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)) + " ";
                } else {
                    for (Character c : smsAddress.toCharArray()) {
                        phraseToSpeak += c + " ";
                    }
                }

                cursor.close();
                phraseToSpeak += ": ";
            } else {
                phraseToSpeak = "unavailable number ";
            }

            if (smsContent != null && !smsContent.isEmpty()) {
                phraseToSpeak += smsContent;
            }
        } else {
            phraseToSpeak = "Message speaking unavailable";
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.UK);
            Log.i("TTS", phraseToSpeak);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
            }

            tts.speak(phraseToSpeak, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Log.e("TTS", "Initialization failed!");
        }
    }
}
