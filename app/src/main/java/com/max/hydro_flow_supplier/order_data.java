package com.max.hydro_flow_supplier;


public class order_data {
    private String orderID;
    private String customerID;
    private String date;
    private String status;
    private String grandTotal;
    private String assigned_area;
    private String invoicepdf;


    public order_data() {
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public order_data(String orderID, String customerID, String date, String status, String assigned_area, String invoicepdf, String grandTotal) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.date = date;
        this.status = status;
        this.assigned_area = assigned_area;
        this.invoicepdf = invoicepdf;
        this.grandTotal = grandTotal;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }


    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getInvoicepdf() {
        return invoicepdf;
    }

    public void setInvoicepdf(String invoicepdf) {
        this.invoicepdf = invoicepdf;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getAssigned_area() {
        return assigned_area;
    }

    public void setAssigned_area(String assigned_area) {
        this.assigned_area = assigned_area;
    }


}
