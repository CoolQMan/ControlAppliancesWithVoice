package com.team17.controlapplianceswithvoice;

public class ApplianceModel {
    String applianceName;
    boolean status = false;

    public ApplianceModel(String name, boolean status){
        this.applianceName = name;
        this.status = status;
    }
}
