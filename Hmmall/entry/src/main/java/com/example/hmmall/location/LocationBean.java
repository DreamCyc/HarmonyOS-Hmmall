
package com.example.hmmall.location;

/**
 * Location Model class
 */
public class LocationBean {
    private double latitude;

    private double longitude;

    private double direction;

    private float speed;

    private float precision;

    private String time;

    private String roadName;

    private String locality;

    private String subAdministrative;

    private String administrative;

    private String countryName;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSubAdministrative() {
        return subAdministrative;
    }

    public void setSubAdministrative(String subAdministrative) {
        this.subAdministrative = subAdministrative;
    }

    public String getAdministrative() {
        return administrative;
    }

    public void setAdministrative(String administrative) {
        this.administrative = administrative;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "LocationDetails{" + "latitude=" + latitude + ", longitude=" + longitude + ", direction=" + direction
                + ", speed=" + speed + ", precision=" + precision + ", time='" + time + '\'' + ", roadName='" + roadName
                + '\'' + ", locality='" + locality + '\'' + ", subAdministrative='" + subAdministrative + '\''
                + ", administrative='" + administrative + '\'' + ", countryName='" + countryName + '\'' + '}';
    }
}
