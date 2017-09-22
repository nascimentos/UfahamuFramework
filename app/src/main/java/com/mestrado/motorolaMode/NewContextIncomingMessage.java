package com.mestrado.motorolaMode;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;

import com.mestrado.ufahamu.NewContext;

import java.util.Date;

/**
 * Created by pma035 on 3/3/17.
 */

public class NewContextIncomingMessage extends NewContext {
    private Bundle bundleIncomingMessage;
    private BroadcastReceiver messageBroadcastReceiver;

    public Bundle getBundleIncomingMessage() {
        return bundleIncomingMessage;
    }

    public void setBundleIncomingMessage(Bundle bundleIncomingMessage) {
        this.bundleIncomingMessage = bundleIncomingMessage;
    }

    public BroadcastReceiver getMessageBroadcastReceiver() {
        return messageBroadcastReceiver;
    }

    @Override
    protected Date doDefineFirstTimeContextCheck() {
        return null;
    }

    @Override
    protected int doDefineContextualCheckTimes() {
        return 0;
    }

    @Override
    protected long doDefineContextCheckFrequency() {
        return 1000;
    }

    @Override
    protected void doCreateNewContext() {
        Log.i(MyActivity.LOG, "Incoming message");
        messageBroadcastReceiver = new BroadcastReceiverIncomingMessage(this);
        IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        intentFilter.setPriority(1000);
        intentFilter.addDataScheme("sms");
        intentFilter.addDataAuthority("*", "6734");
        getRules().iterator().next().getParentActivity().registerReceiver(messageBroadcastReceiver,
                intentFilter);
    }

    //tirar o registro do broadcast em algum momento.
}
