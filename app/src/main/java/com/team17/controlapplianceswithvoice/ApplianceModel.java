package com.team17.controlapplianceswithvoice;

public class ApplianceModel {
    private int applianceId;
    private String applianceName;
    private boolean status;

    // Constructor
    public ApplianceModel(int applianceId, String applianceName, boolean status) {
        this.applianceId = applianceId;
        this.applianceName = applianceName;
        this.status = status;
    }

    // Getters and Setters
    public int getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(int applianceId) {
        this.applianceId = applianceId;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

