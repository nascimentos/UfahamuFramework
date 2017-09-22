package com.mestrado.ufahamu;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.DetectedActivity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pma035 on 2/15/17.
 */

public abstract class Context implements Serializable, GoogleApiClient.ConnectionCallbacks {
    private int contextEventCheckingTimes;
    private int contextEventCheckingCounter;
    private boolean isTimerTaskRunning;
    private GoogleApiClient thisGoogleApiClient;
    private Integer identifier;
    private Date startDate;
    private Date stopDate;
    private Location location;
    private Double radius;
    private Integer userActivity;
    private Integer headphoneState;
    private String awarenessAPIContextType;
    private ContextState state;
    private Set<Rule> rules;
    private Timer timer;
    private TimerTask timerTask;
    private final String ACTIVITY_KEY = "Activity";
    private final String LOCATION_KEY = "Location";
    private final String HEADPHONE_KEY = "Headphone";
    private final String DATE_KEY = "Date";
    public static final String TESTE = "TESTE";
    public static final String LOG = "LOG_TESTE";

    public Context() {
        contextEventCheckingTimes = doDefineContextualCheckTimes();
        contextEventCheckingCounter = 0;
        isTimerTaskRunning = false;
        awarenessAPIContextType = null;
        state = ContextState.UNCHECKED;
        rules = new HashSet<>();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                isTimerTaskRunning = true;

                if (Context.this instanceof AwarenessAPIContext) {
                    Log.i(LOG, "Awareness Monitoring...");
                    switch (awarenessAPIContextType) {
                        case DATE_KEY:
                            verifyDateContext();
                            break;
                        case LOCATION_KEY:
                            verifyLocationContext();
                            break;
                        case ACTIVITY_KEY:
                            verifyUserActivityContext();
                            break;
                        case HEADPHONE_KEY:
                            verifyHeadphoneStateContext();
                    }
                } else if (Context.this instanceof NewContext) {
                    Log.i(LOG, "New Monitoring...");
                    doCreateNewContext();
                }
            }
        };
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setDateInterval(Date startDate, Date stopDate) {
        this.startDate = startDate;
        this.stopDate = stopDate;

        if (awarenessAPIContextType == null) {
            awarenessAPIContextType = DATE_KEY;
        }
    }

    public Location getLocation() {
        return location;
    }

    public Double getRadius() {
        return radius;
    }

    public void setLocationAndRadius(Location location, Double radius) {
        this.location = location;
        this.radius = radius;

        if (awarenessAPIContextType == null) {
            awarenessAPIContextType = LOCATION_KEY;
            //Log.i(AwarenessAPIContextTest1.TESTE, "Context set: Location");
        }
    }

    public Integer getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(Integer userActivity) {
        this.userActivity = userActivity;

        if (awarenessAPIContextType == null) {
            awarenessAPIContextType = ACTIVITY_KEY;
            //Log.i(AwarenessAPIContextTest1.TESTE, "Context set: Activity");
        }
    }

    public Integer getHeadphoneState() {
        return headphoneState;
    }

    public void setHeadphoneState(Integer headphoneState) {
        this.headphoneState = headphoneState;

        if (awarenessAPIContextType == null) {
            awarenessAPIContextType = HEADPHONE_KEY;
            //Log.i(AwarenessAPIContextTest1.TESTE, "Context set: Headphone state");
        }
    }

    public ContextState getState() {
        //Log.i(AwarenessAPIContextTest1.TESTE, awarenessAPIContextType + " " + state);
        return state;
    }

    public void setState(ContextState state) {
        ContextState priorState = this.state;
        this.state = state;

        if (this.state != priorState && this.state == ContextState.CHECKED) {
            notifyAllRules();
        }
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public void startContextMonitoring(Rule rule) {
        rules.add(rule);

        if (this instanceof AwarenessAPIContext) {
            thisGoogleApiClient = new GoogleApiClient.Builder(rule.getParentActivity())
                    .addApi(Awareness.API).addConnectionCallbacks(this).build();
            thisGoogleApiClient.connect();
        } else if (this instanceof NewContext) {
            scheduleTimer();
        }
    }

    private void scheduleTimer() {
        if (!isTimerTaskRunning) {
            Date firstTime = doDefineFirstTimeContextCheck();

            try {
                if (firstTime != null) {
                    timer.scheduleAtFixedRate(timerTask, firstTime, doDefineContextCheckFrequency());
                } else {
                    timer.scheduleAtFixedRate(timerTask, 0, doDefineContextCheckFrequency());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        doSetAwarenessAPIContexts();
        scheduleTimer();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void verifyDateContext() {
        Date actualTime = new Date();
        Log.i(LOG, "Start date: " + startDate.toString());
        Log.i(LOG, "Actual date: " + actualTime.toString());
        Log.i(LOG, "Stop date: " + stopDate.toString());

        if (startDate != null && stopDate != null) {
            if (actualTime.after(startDate) && actualTime.before(stopDate)) {
                setState(ContextState.CHECKED);
            } else {
                setState(ContextState.UNCHECKED);
            }
        }
    }

    private void verifyLocationContext() {
        try {
            Awareness.SnapshotApi.getLocation(thisGoogleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult != null && locationResult.getLocation() != null) {
                                Log.i(LOG, locationResult.getLocation().toString());
                                Log.i(LOG, String.valueOf(locationResult.getLocation().distanceTo(location)));
                            }

                            if (location != null
                                    && radius != null
                                    && locationResult != null
                                    && locationResult.getLocation() != null) {
                                if (locationResult.getLocation().distanceTo(location) <= radius) {
                                    setState(ContextState.CHECKED);
                                } else {
                                    setState(ContextState.UNCHECKED);
                                }
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void verifyUserActivityContext() {
        Awareness.SnapshotApi.getDetectedActivity(thisGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        DetectedActivity detectedActivity = detectedActivityResult
                                .getActivityRecognitionResult().getMostProbableActivity();
                        //Log.i(AwarenessAPIContextTest1.TESTE, detectedActivity.toString());

                        if (userActivity != null && detectedActivity != null) {
                            if (detectedActivity.getType() == userActivity
                                    && detectedActivity.getConfidence() >= 50) {
                                setState(ContextState.CHECKED);
                            } else {
                                setState(ContextState.UNCHECKED);
                            }
                        }
                    }
                });
    }

    private void verifyHeadphoneStateContext() {
        Awareness.SnapshotApi.getHeadphoneState(thisGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        //Log.i(AwarenessAPIContextTest1.TESTE, headphoneStateResult.getHeadphoneState().toString());

                        if (headphoneState != null
                                && headphoneStateResult != null) {
                            if (headphoneStateResult.getHeadphoneState().getState() == headphoneState) {
                                setState(ContextState.CHECKED);
                            } else {
                                setState(ContextState.UNCHECKED);
                            }
                        }
                    }
                });
    }

    private void notifyAllRules() {
        for (Rule r : rules) {
            if (r != null) {
                r.update();
            }
        }

        if (++contextEventCheckingCounter == contextEventCheckingTimes) {
            stopContextMonitoring();
        }
    }

    public void stopContextMonitoring() {
        timerTask.cancel();
        timerTask = null;
    }

    protected abstract Date doDefineFirstTimeContextCheck();

    protected abstract int doDefineContextualCheckTimes();

    protected abstract long doDefineContextCheckFrequency();

    protected abstract void doSetAwarenessAPIContexts();

    protected abstract void doCreateNewContext();
}
