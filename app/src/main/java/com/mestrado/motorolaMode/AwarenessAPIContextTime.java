package com.mestrado.motorolaMode;

import android.util.Log;

import com.mestrado.ufahamu.AwarenessAPIContext;

import java.util.Date;

/**
 * Created by pma035 on 2/24/17.
 */

public class AwarenessAPIContextTime extends AwarenessAPIContext {
    private Date initialTime;
    private Date finalTime;

    public AwarenessAPIContextTime(Date initialTime, Date finalTime) {
        this.initialTime = initialTime;
        this.finalTime = finalTime;
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
    Verifica o contexto com frequência de 1h (mais um segundo) = 3600s
     */
    @Override
    protected long doDefineContextCheckFrequency() {
        return 3601000;
    }

    @Override
    protected void doSetAwarenessAPIContexts() {
        Log.i(MyActivity.LOG, "AwarenessAPIContextTime: " + initialTime + " " + finalTime);
        setDateInterval(initialTime, finalTime);
    }
}
