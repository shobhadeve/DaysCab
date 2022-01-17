package com.dayscab.user.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelCar {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("car_name")
    @Expose
    private String carName;
    @SerializedName("car_image")
    @Expose
    private String carImage;
    @SerializedName("charge")
    @Expose
    private String charge;
    @SerializedName("no_of_seats")
    @Expose
    private String noOfSeats;
    @SerializedName("min_charge")
    @Expose
    private String minCharge;
    @SerializedName("per_km")
    @Expose
    private String perKm;
    @SerializedName("hold_charge")
    @Expose
    private String holdCharge;
    @SerializedName("ride_time_charge_permin")
    @Expose
    private String rideTimeChargePermin;
    @SerializedName("service_tax")
    @Expose
    private String serviceTax;
    @SerializedName("free_time_min")
    @Expose
    private String freeTimeMin;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("miles")
    @Expose
    private String miles;
    @SerializedName("perMin")
    @Expose
    private Integer perMin;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("cab_find")
    @Expose
    private String cabFind;

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(String noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public String getMinCharge() {
        return minCharge;
    }

    public void setMinCharge(String minCharge) {
        this.minCharge = minCharge;
    }

    public String getPerKm() {
        return perKm;
    }

    public void setPerKm(String perKm) {
        this.perKm = perKm;
    }

    public String getHoldCharge() {
        return holdCharge;
    }

    public void setHoldCharge(String holdCharge) {
        this.holdCharge = holdCharge;
    }

    public String getRideTimeChargePermin() {
        return rideTimeChargePermin;
    }

    public void setRideTimeChargePermin(String rideTimeChargePermin) {
        this.rideTimeChargePermin = rideTimeChargePermin;
    }

    public String getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(String serviceTax) {
        this.serviceTax = serviceTax;
    }

    public String getFreeTimeMin() {
        return freeTimeMin + " Min";
    }

    public void setFreeTimeMin(String freeTimeMin) {
        this.freeTimeMin = freeTimeMin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public Integer getPerMin() {
        return perMin;
    }

    public void setPerMin(Integer perMin) {
        this.perMin = perMin;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCabFind() {
        return cabFind.equalsIgnoreCase("no_cab") ? "No Cab Found" : cabFind.concat(" Min");
    }

    public void setCabFind(String cabFind) {
        this.cabFind = cabFind;
    }
}
