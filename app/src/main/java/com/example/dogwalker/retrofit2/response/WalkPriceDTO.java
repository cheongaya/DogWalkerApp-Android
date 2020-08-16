package com.example.dogwalker.retrofit2.response;

public class WalkPriceDTO {

    int price_thirty_minutes;
    int price_sixty_minutes;
    int addprice_large_size;
    int addprice_one_dog;
    int addprice_holiday;

    public WalkPriceDTO(int price_thirty_minutes, int price_sixty_minutes, int addprice_large_size, int addprice_one_dog, int addprice_holiday) {
        this.price_thirty_minutes = price_thirty_minutes;
        this.price_sixty_minutes = price_sixty_minutes;
        this.addprice_large_size = addprice_large_size;
        this.addprice_one_dog = addprice_one_dog;
        this.addprice_holiday = addprice_holiday;
    }

    public int getPrice_thirty_minutes() {
        return price_thirty_minutes;
    }

    public void setPrice_thirty_minutes(int price_thirty_minutes) {
        this.price_thirty_minutes = price_thirty_minutes;
    }

    public int getPrice_sixty_minutes() {
        return price_sixty_minutes;
    }

    public void setPrice_sixty_minutes(int price_sixty_minutes) {
        this.price_sixty_minutes = price_sixty_minutes;
    }

    public int getAddprice_large_size() {
        return addprice_large_size;
    }

    public void setAddprice_large_size(int addprice_large_size) {
        this.addprice_large_size = addprice_large_size;
    }

    public int getAddprice_one_dog() {
        return addprice_one_dog;
    }

    public void setAddprice_one_dog(int addprice_one_dog) {
        this.addprice_one_dog = addprice_one_dog;
    }

    public int getAddprice_holiday() {
        return addprice_holiday;
    }

    public void setAddprice_holiday(int addprice_holiday) {
        this.addprice_holiday = addprice_holiday;
    }
}
