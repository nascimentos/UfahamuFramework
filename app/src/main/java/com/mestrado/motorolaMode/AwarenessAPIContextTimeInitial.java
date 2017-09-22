package com.mestrado.motorolaMode;

import android.util.Log;

import com.mestrado.ufahamu.AwarenessAPIContext;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by pma035 on 2/24/17.
 */

public class AwarenessAPIContextTimeInitial extends AwarenessAPIContext {
    private Date initialTime;
    private Date finalTime;

    public AwarenessAPIContextTimeInitial(Date initialTime) {
        this.initialTime = initialTime;
        Calendar c = Calendar.getInstance();
        c.setTime(initialTime);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 50);
        this.finalTime = c.getTime();
    }

    public Date getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(Date initialTime) {
        this.initialTime = initialTime;
    }

    public Date getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(Date finalTime) {
        this.finalTime = finalTime;
    }

    @Override
    protected Date doDefineFirstTimeContextCheck() {
        return initialTime;
    }

    @Override
    protected int doDefineContextualCheckTimes() {
        return 0;
    }

    /*
    Verifica o contexto com frequência de 24h (mais um segundo) = 86400s
     */
    @Override
    protected long doDefineContextCheckFrequency() {
        return 86401000;
    }

    @Override
    protected void doSetAwarenessAPIContexts() {
        Log.i(MyActivity.LOG, "AwarenessAPIContextTimeInitial: " + initialTime + " " + finalTime);
        setDateInterval(initialTime, finalTime);
    }
}
