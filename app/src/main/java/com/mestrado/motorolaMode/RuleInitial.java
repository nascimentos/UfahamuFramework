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

public class RuleInitial extends Rule {
    private Date initialTime;

    public RuleInitial(Activity parentActivity, Date initialTime) {
        super(parentActivity);
        this.initialTime = initialTime;
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

        l.add(new ActionSilenceMode());

        return l;
    }
}
