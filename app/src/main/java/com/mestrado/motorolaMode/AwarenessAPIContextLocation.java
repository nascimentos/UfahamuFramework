package com.mestrado.motorolaMode;

import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.mestrado.ufahamu.AwarenessAPIContext;

import java.util.Date;

/**
 * Created by pma035 on 3/2/17.
 */

public class AwarenessAPIContextLocation extends AwarenessAPIContext {
    private Place place;

    public AwarenessAPIContextLocation(Place place) {
        this.place = place;
    }

    @Override
    protected Date doDefineFirstTimeContextCheck() {
        return null;
    }

    @Override
    protected int doDefineContextualCheckTimes() {
        return 0;
    }

    //Verifica a cada 2min
    @Override
    protected long doDefineContextCheckFrequency() {
        return 120000;
    }

    @Override
    protected void doSetAwarenessAPIContexts() {
        Location location = new Location("");
        double radius = 50.;

        //ITV
        /*location.setLongitude(-48.482215);
        location.setLatitude(-1.446906);*/

        if (place != null && place.getLatLng() != null) {
            //Log.i(MyActivity.LOG, "set awareness context: " + place.getLatLng().longitude + ", "
              //      + place.getLatLng().latitude);
            location.setLongitude(place.getLatLng().longitude);
            location.setLatitude(place.getLatLng().latitude);
            setLocationAndRadius(location, radius);
        } else {
            Toast.makeText(getRules().iterator().next().getParentActivity(), "Can not set location",
                    Toast.LENGTH_LONG).show();
        }
    }
}
