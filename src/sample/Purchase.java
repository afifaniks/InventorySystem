package sample;

import java.time.LocalDate;

/**
 * Author: Afif Al Mamun
 * Written on: 7/28/2018
 * Project: TeslaRentalInventory
 **/
public class Purchase {
    int purID;
    int cusID;
    int itemID;
    String date;
    int qty;
    double price, paid, due;

    public Purchase(int purID, int cusID, int itemID, String date, int qty, double paid, double due) {
        this.purID = purID;
        this.cusID = cusID;
        this.itemID = itemID;
        this.date = date;
        this.qty = qty;
        this.paid = paid;
        this.due = due;
    }

    public int getPurID() {
        return purID;
    }

    public void setPurID(int purID) {
        this.purID = purID;
    }

    public int getCusID() {
        return cusID;
    }

    public void setCusID(int cusID) {
        this.cusID = cusID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }
}
