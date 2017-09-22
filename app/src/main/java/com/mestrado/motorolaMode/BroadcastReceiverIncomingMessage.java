package com.mestrado.motorolaMode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.util.Log;

import com.mestrado.ufahamu.ContextState;

/**
 * Created by pma035 on 3/3/17.
 */

public class BroadcastReceiverIncomingMessage extends BroadcastReceiver {
    private NewContextIncomingMessage userContext;

    public BroadcastReceiverIncomingMessage() {
    }

    public BroadcastReceiverIncomingMessage(NewContextIncomingMessage userContext) {
        this.userContext = userContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(MyActivity.LOG, "Message received");

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Log.i(MyActivity.LOG, "State incoming message checked");
            userContext.setState(ContextState.CHECKED);
            userContext.setBundleIncomingMessage(intent.getExtras());
        } else {
            userContext.setState(ContextState.UNCHECKED);
        }
    }
}
