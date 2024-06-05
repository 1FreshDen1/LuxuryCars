package com.example.coursework;

import android.os.Parcel;
import android.os.Parcelable;

public class CarAd implements Parcelable {
    private String carId;
    private String details;
    private String price;
    private String year;
    private String color;
    private String engine;
    private String tax;
    private String gearBox;
    private String gear;
    private String isNew;
    private String imageUrl;

    // Конструктор без параметров (по умолчанию)
    public CarAd() {
    }

    // Конструктор с параметрами
    public CarAd(String carId, String details, String price, String year, String color, String engine, String tax, String gearBox, String gear, String isNew, String imageUrl) {
        this.carId = carId;
        this.details = details;
        this.price = price;
        this.year = year;
        this.color = color;
        this.engine = engine;
        this.tax = tax;
        this.gearBox = gearBox;
        this.gear = gear;
        this.isNew = isNew;
        this.imageUrl = imageUrl;
    }

    // Геттеры и сеттеры
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getGearBox() {
        return gearBox;
    }

    public void setGearBox(String gearBox) {
        this.gearBox = gearBox;
    }

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public boolean isNew() {
        return "Новый".equalsIgnoreCase(isNew);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected CarAd(Parcel in) {
        carId = in.readString();
        details = in.readString();
        price = in.readString();
        year = in.readString();
        color = in.readString();
        engine = in.readString();
        tax = in.readString();
        gearBox = in.readString();
        gear = in.readString();
        isNew = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<CarAd> CREATOR = new Creator<CarAd>() {
        @Override
        public CarAd createFromParcel(Parcel in) {
            return new CarAd(in);
        }

        @Override
        public CarAd[] newArray(int size) {
            return new CarAd[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carId);
        dest.writeString(details);
        dest.writeString(price);
        dest.writeString(year);
        dest.writeString(color);
        dest.writeString(engine);
        dest.writeString(tax);
        dest.writeString(gearBox);
        dest.writeString(gear);
        dest.writeString(isNew);
        dest.writeString(imageUrl);
    }
}
