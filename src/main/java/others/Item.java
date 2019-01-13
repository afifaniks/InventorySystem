package main.java.others;

/**
 * Author: Afif Al Mamun
 * Written on: 7/24/2018
 * Project: TeslaRentalInventory
 **/
public class Item {
    int id;
    String name;
    int stock;
    boolean rent, sale;
    double salePrice;
    double rentRate;
    String photo;
    String itemType;

    public Item(int id, String name, int stock, boolean rent, boolean sale, double salePrice, double rentRate, String photo, String itemType) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.rent = rent;
        this.sale = sale;
        this.salePrice = salePrice;
        this.rentRate = rentRate;
        this.photo = photo;
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isRent() {
        return rent;
    }

    public void setRent(boolean rent) {
        this.rent = rent;
    }

    public boolean isSale() {
        return sale;
    }

    public void setSale(boolean sale) {
        this.sale = sale;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getRentRate() {
        return rentRate;
    }

    public void setRentRate(double rentRate) {
        this.rentRate = rentRate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
