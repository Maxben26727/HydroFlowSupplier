package com.max.hydro_flow_supplier;

public class customer_data {
    private String customerID,name, address, email, pancardNo, gstinNo, contactno, picurl ,areaName,balance;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public customer_data(String customerID, String name, String address, String email, String pancardNo, String gstinNo, String contactno, String picurl, String areaName, String balance) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        this.email = email;
        this.pancardNo = pancardNo;
        this.gstinNo = gstinNo;
        this.contactno = contactno;
        this.picurl = picurl;
        this.areaName = areaName;
        this.balance = balance;
    }
public customer_data(){}
    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getPancardNo() {
        return pancardNo;
    }

    public void setPancardNo(String pancardNo) {
        this.pancardNo = pancardNo;
    }

    public String getGstinNo() {
        return gstinNo;
    }

    public void setGstinNo(String gstinNo) {
        this.gstinNo = gstinNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
