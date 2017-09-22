package com.mestrado.motorolaMode;

import android.util.Log;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Context;
import com.mestrado.ufahamu.Rule;

import java.util.Calendar;

/**
 * Created by pma035 on 2/24/17.
 */

public class ActionUpdateDate extends Action {
    private Context context;

    public ActionUpdateDate(Context context) {
        this.context = context;
    }

    @Override
    public void doAction(Rule ruleReceived) {
        Log.i(MyActivity.LOG, "Action update date");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(context.getStartDate());
        c2.setTime(context.getStopDate());
        c1.set(Calendar.DAY_OF_MONTH, c1.get(Calendar.DAY_OF_MONTH) + 1);
        c2.set(Calendar.DAY_OF_MONTH, c2.get(Calendar.DAY_OF_MONTH) + 1);
        context.setDateInterval(c1.getTime(), c2.getTime());
        Log.i(MyActivity.LOG, "New start date: " + c1.getTime());
        Log.i(MyActivity.LOG, "New stop date: " + c2.getTime());
    }
}
