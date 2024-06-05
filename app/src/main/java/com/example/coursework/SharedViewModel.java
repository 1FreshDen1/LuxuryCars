package com.example.coursework;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<CarAd>> carAds = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<CarAd>> getCarAds() {
        return carAds;
    }

    public void addCarAd(CarAd carAd) {
        List<CarAd> currentAds = carAds.getValue();
        if (currentAds != null) {
            currentAds.add(carAd);
            carAds.setValue(currentAds);
        }
    }

    public void removeCarAd(int position) {
        List<CarAd> currentAds = carAds.getValue();
        if (currentAds != null && position >= 0 && position < currentAds.size()) {
            currentAds.remove(position);
            carAds.setValue(currentAds);
        }
    }

    public void setCarAds(List<CarAd> carAds) {
        this.carAds.setValue(carAds);
    }

    public void clearCarAds() {
        carAds.setValue(new ArrayList<>());
    }
}
