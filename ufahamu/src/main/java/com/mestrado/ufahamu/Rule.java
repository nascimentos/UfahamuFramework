package com.mestrado.ufahamu;

import android.app.Activity;

import java.util.List;

/**
 * Created by pma035 on 2/15/17.
 */

public abstract class Rule {

    private Activity parentActivity;
    private List<Context> contexts;
    private List<Action> actions;

    public Rule(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public Activity getParentActivity() {
        return parentActivity;
    }

    public List<Context> getContexts() {
        return contexts;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void startContextsMonitoring() {
        contexts = doDefineContextList();
        actions = doDefineActionList();

        if (contexts != null) {
            for (Context c : contexts) {
                if (c != null) {
                    c.startContextMonitoring(this);
                }
            }
        }
    }

    public void update() {
        if (contexts != null
                && actions != null
                && doCondition()) {
            boolean existAtLeastOneContextUnchecked = false;

            for (Context c : contexts) {
                if (c.getState().equals(ContextState.UNCHECKED)) {
                    existAtLeastOneContextUnchecked = true;
                    break;
                }
            }

            if (!existAtLeastOneContextUnchecked) {
                initiateActions();
                //stopContextsMonitoring();
            }
        }
    }

    private void initiateActions() {
        for (final Action a : actions) {
            if (a != null) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        a.doAction(Rule.this);
                    }
                });
            }
        }
    }

    public void stopContextsMonitoring() {
        for (Context c : contexts) {
            c.stopContextMonitoring();
        }
    }

    public void killContextsAndActions() {
        stopContextsMonitoring();
        contexts = null;
        actions = null;
    }

    protected abstract boolean doCondition();

    protected abstract List<Context> doDefineContextList();

    protected abstract List<Action> doDefineActionList();
}
