package com.max.hydro_flow_supplier;

public class transaction_data {

        String paymentID;
        String cname;

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        String orderID;
        String customerID;
        String amount;
        String date;
        String sign;

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    String paidAmount;
    public transaction_data() {
    }
        public transaction_data(String paymentID,String cname ,String orderID, String customerID, String amount,String paidAmount, String date, String sign) {
            this.paymentID = paymentID;
            this.cname=cname;
            this.orderID = orderID;
            this.customerID = customerID;
            this.amount = amount;
            this.paidAmount = paidAmount;
            this.date = date;
            this.sign = sign;
        }

        public String getPaymentID() {
            return paymentID;
        }

        public void setPaymentID(String paymentID) {
            this.paymentID = paymentID;
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

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
}
