package com.exposure.handlers;

import android.util.Log;

import com.exposure.user.CurrentUser;

public class DistanceHandler {
    private final static double EARTH_RADIUS = 6371;

    public static int distanceInKM(CurrentUser a, CurrentUser b) {
        if (a.getLocation().size() != 2 || b.getLocation().size() != 2) {
            Log.d("DISTANCE", "One user doesn't have location data!");
            return 10000;
        }

        double aLat = a.getLocation().get("Latitude");
        double aLng = a.getLocation().get("Longitude");

        double bLat = b.getLocation().get("Latitude");
        double bLng = b.getLocation().get("Longitude");

        double latDistance = Math.toRadians(aLat - bLat);
        double lngDistance = Math.toRadians(aLng - bLng);

        double x = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(aLat)) * Math.cos(Math.toRadians(bLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));

        int distance = (int) Math.round(EARTH_RADIUS * c);

        Log.d("DISTANCE", String.valueOf(distance));

        return distance;
    }
}
