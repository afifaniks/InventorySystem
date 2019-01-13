package main.java.others;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
public class Transact {
    Integer trID;
    String date;
    Integer accID;
    Integer purchaseOrRentID;
    String issuedBy;

    public Transact(Integer trID, String date, Integer accID, Integer purchaseOrRentID, String issuedBy) {
        this.trID = trID;
        this.date = date;
        this.accID = accID;
        this.purchaseOrRentID = purchaseOrRentID;
        this.issuedBy = issuedBy;
    }

    public Integer getTrID() {
        return trID;
    }

    public void setTrID(Integer trID) {
        this.trID = trID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAccID() {
        return accID;
    }

    public void setAccID(Integer accID) {
        this.accID = accID;
    }

    public Integer getPurchaseOrRentID() {
        return purchaseOrRentID;
    }

    public void setPurchaseOrRentID(Integer purchaseOrRentID) {
        this.purchaseOrRentID = purchaseOrRentID;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }
}
