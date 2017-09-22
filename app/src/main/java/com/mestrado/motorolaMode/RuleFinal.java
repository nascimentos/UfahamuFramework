package com.mestrado.motorolaMode;

import android.app.Activity;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Context;
import com.mestrado.ufahamu.Rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pma035 on 2/24/17.
 */

public class RuleFinal extends Rule {
    private Date initialTime;
    private Context context;

    public RuleFinal(Activity parentActivity, Context context, Date initialTime) {
        super(parentActivity);
        this.initialTime = initialTime;
        this.context = context;
    }

    @Override
    protected boolean doCondition() {
        return true;
    }

    @Override
    protected List<Context> doDefineContextList() {
        List<Context> l = new ArrayList<>();

        l.add(new AwarenessAPIContextTimeInitial(initialTime));

        return l;
    }

    @Override
    protected List<Action> doDefineActionList() {
        List<Action> l = new ArrayList<>();

        l.add(new ActionNormalMode());
        l.add(new ActionUpdateDate(context));

        return l;
    }
}
