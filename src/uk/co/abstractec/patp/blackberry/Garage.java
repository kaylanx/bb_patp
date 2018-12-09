package uk.co.abstractec.patp.blackberry;

import javax.microedition.location.Coordinates;
import javax.microedition.location.QualifiedCoordinates;
import java.util.Date;

public class Garage {

    private String name;
    private Date created;
    private String createdBy;
    private String description;
    private double latitude;
    private double longitude;
    private String link;
    private float distance;
    private Coordinates coords;

    public Garage() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = new Date(created.getTime());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getDistanceInMiles() {
        double tmp = distance / 1609.344f;
        return (int) Math.floor(tmp + 0.5f);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }


    public Coordinates getCoordinates() {
        return coords;
    }

    public void setCoordinates(Coordinates coords) {
        this.coords = coords;
    }

    // Hmm
    public void calcDistance(QualifiedCoordinates coords) {
        this.coords = new Coordinates(latitude, longitude, coords.getAltitude());
        distance = coords.distance(this.coords);
    }

    public Garage copy() {
        Garage garage = new Garage();
        garage.setCreated(getCreated());
        garage.setCreatedBy(getCreatedBy());
        garage.setDescription(getDescription());
        garage.setDistance(getDistance());
        garage.setLatitude(getLatitude());
        garage.setLink(getLink());
        garage.setLongitude(getLongitude());
        garage.setName(getName());
        garage.setCoordinates(getCoordinates());
        return garage;
    }

}
