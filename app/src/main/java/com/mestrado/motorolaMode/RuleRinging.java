package com.mestrado.motorolaMode;

import android.app.Activity;

import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Context;
import com.mestrado.ufahamu.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pma035 on 2/24/17.
 */

public class RuleRinging extends Rule {
    private Context context1;

    public RuleRinging(Activity parentActivity, Context context1) {
        super(parentActivity);
        this.context1 = context1;
    }

    @Override
    protected boolean doCondition() {
        return true;
    }

    @Override
    protected List<Context> doDefineContextList() {
        List<Context> l = new ArrayList<>();

        l.add(context1);
        l.add(new NewContextFavouriteCall());

        return l;
    }

    @Override
    protected List<Action> doDefineActionList() {
        List<Action> l = new ArrayList<>();

        l.add(new ActionNormalRingtoneMode());

        return l;
    }
}
