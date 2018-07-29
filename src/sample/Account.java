package sample;

/**
 * Author: Afif Al Mamun
 * Written on: 7/30/2018
 * Project: TeslaRentalInventory
 **/
public class Account {
    int accID;
    String cusName;
    String accName;
    String paymethod;

    public Account(int accID, String cusName, String accName, String paymethod) {
        this.accID = accID;
        this.cusName = cusName;
        this.accName = accName;
        this.paymethod = paymethod;
    }

    public int getAccID() {
        return accID;
    }

    public void setAccID(int accID) {
        this.accID = accID;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getPaymethod() {
        return paymethod;
    }

    public void setPaymethod(String paymethod) {
        this.paymethod = paymethod;
    }

}
