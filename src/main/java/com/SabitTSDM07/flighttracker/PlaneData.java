package com.SabitTSDM07.flighttracker;

public class PlaneData {
    public final String icao24;
    public final String callsign;
    public final String originCountry;
    public final double latitude;
    public final double longitude;
    public final double altitudeMeters;
    public final double velocity;  // in m/s
    public final double headingDeg;
    public final long timestamp;
    public final String fromAirport;  // optional, for future
    public final String toAirport;    // optional, for future

    public PlaneData(String icao24, String callsign, String originCountry, double latitude, double longitude,
                     double altitudeMeters, double velocity, double headingDeg, long timestamp,
                     String fromAirport, String toAirport) {
        this.icao24 = icao24;
        this.callsign = callsign;
        this.originCountry = originCountry;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitudeMeters = altitudeMeters;
        this.velocity = velocity;
        this.headingDeg = headingDeg;
        this.timestamp = timestamp;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
    }
}
