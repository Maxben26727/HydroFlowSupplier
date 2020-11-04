package com.max.hydro_flow_supplier;

public class user_data {

    String delboyID;
    String name;
    String address;
    String email;
    String contactno;
    String dob;
    String aadharno;
    String salary;
    String picurl;
    String areaName;
public user_data(){}
    public user_data(String delboyID, String name, String address, String email, String contactno, String dob, String aadharno, String salary, String picurl, String areaName) {
        this.delboyID = delboyID;
        this.name = name;
        this.address = address;
        this.email = email;
        this.contactno = contactno;
        this.dob = dob;
        this.aadharno = aadharno;
        this.salary = salary;
        this.picurl = picurl;
        this.areaName = areaName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDelboyID() {
        return delboyID;
    }

    public void setDelboyID(String delboyID) {
        this.delboyID = delboyID;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAadharno() {
        return aadharno;
    }

    public void setAadharno(String aadharno) {
        this.aadharno = aadharno;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }
}
