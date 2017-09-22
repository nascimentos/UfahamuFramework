package com.mestrado.motorolaMode;

import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mestrado.ufahamu.ContextState;
import com.mestrado.ufahamu.NewContext;

import java.util.Date;

/**
 * Created by pma035 on 3/2/17.
 */

public class NewContextRingingCallState extends NewContext {
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
        return 3600000;
    }

    @Override
    protected void doCreateNewContext() {
        TelephonyManager myTelephonyManager = (TelephonyManager) getRules().iterator().next()
                .getParentActivity().getSystemService(android.content.Context.TELEPHONY_SERVICE);

        Looper.prepare();
        myTelephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                //Log.i(MyActivity.LOG, "Phone ringing");

                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    ((RuleHome) getRules().iterator().next()).setIncomingNumber(incomingNumber);
                    setState(ContextState.CHECKED);
                } else {
                    setState(ContextState.UNCHECKED);
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
        Looper.loop();
    }
}
