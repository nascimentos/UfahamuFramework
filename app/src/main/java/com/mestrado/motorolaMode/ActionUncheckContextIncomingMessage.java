package com.mestrado.motorolaMode;

import android.util.Log;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Context;
import com.mestrado.ufahamu.ContextState;
import com.mestrado.ufahamu.Rule;

/**
 * Created by pma035 on 3/7/17.
 */

public class ActionUncheckContextIncomingMessage extends Action {
    @Override
    public void doAction(Rule ruleReceived) {
        Log.i(MyActivity.LOG, "Action uncheck message");

        for(Context c : ruleReceived.getContexts()){
            if(c instanceof NewContextIncomingMessage){
                c.setState(ContextState.UNCHECKED);
                Log.i(MyActivity.LOG, c.getState().toString());
            }
        }
    }
}
