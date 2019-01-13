package main.java.others;

/**
 * Author: Afif Al Mamun
 * Written on: 7/29/2018
 * Project: TeslaRentalInventory
 **/
public class Rent {
    int rentID;
    int itemID;
    int cusID;
    String rentDate;
    String returnDate;
    double payAmount, amountDue;
    String user;

    public Rent(int rentID, int itemID, int cusID, String rentDate, String returnDate, double payAmount, double amountDue) {
        this.rentID = rentID;
        this.itemID = itemID;
        this.cusID = cusID;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.payAmount = payAmount;
        this.amountDue = amountDue;
    }

    public Rent(int rentID, int itemID, int cusID, String rentDate, String returnDate, double payAmount, double amountDue, String user) {
        this.rentID = rentID;
        this.itemID = itemID;
        this.cusID = cusID;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.payAmount = payAmount;
        this.amountDue = amountDue;
        this.user = user;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRentID() {
        return rentID;
    }

    public void setRentID(int rentID) {
        this.rentID = rentID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getCusID() {
        return cusID;
    }

    public void setCusID(int cusID) {
        this.cusID = cusID;
    }

    public String getRentDate() {
        return rentDate;
    }

    public void setRentDate(String rentDate) {
        this.rentDate = rentDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }
}
