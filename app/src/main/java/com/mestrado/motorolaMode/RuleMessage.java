package com.mestrado.motorolaMode;

import android.app.Activity;

import com.google.android.gms.location.places.Place;
import com.mestrado.ufahamu.Action;
import com.mestrado.ufahamu.Context;
import com.mestrado.ufahamu.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pma035 on 3/7/17.
 */

public class RuleMessage extends Rule {
    private Place place;

    public RuleMessage(Activity parentActivity, Place place) {
        super(parentActivity);
        this.place = place;
    }

    @Override
    protected boolean doCondition() {
        return true;
    }

    @Override
    protected List<Context> doDefineContextList() {
        List<Context> l = new ArrayList<>();

        l.add(new AwarenessAPIContextLocation(place));
        l.add(new NewContextIncomingMessage());

        return l;
    }

    @Override
    protected List<Action> doDefineActionList() {
        List<Action> l = new ArrayList<>();

        l.add(new ActionSpeakMessage());
        l.add(new ActionUncheckContextIncomingMessage());

        return l;
    }
}
