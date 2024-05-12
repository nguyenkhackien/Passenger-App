package com.example.passengerapp.tools;

import android.app.Activity;
import android.location.Location;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.text.NumberFormat;
import java.util.Locale;

public class Tool {
    public static LatLng getLatLngFromString(String location) {
        LatLng latLng;

        String tempString = location.substring(location.indexOf("(")+1, location.indexOf(")"));
        String[] arrOfStr = tempString.split(",", 2);

        latLng = new LatLng(Double.parseDouble(arrOfStr[0]), Double.parseDouble(arrOfStr[1]));

        return latLng;
    }

    public static String getShortTime(String fullTime) {
        return fullTime.substring(fullTime.indexOf(" ") + 1, fullTime.indexOf("GMT") - 1);
    }

    public static double calculateDistance(LatLng origin, LatLng dest) {
        Location locationOri = new Location("LocationA");
        locationOri.setLatitude(origin.latitude);
        locationOri.setLongitude(origin.longitude);

        Location locationDest = new Location("LocationA");
        locationDest.setLatitude(dest.latitude);
        locationDest.setLongitude(dest.longitude);

        return locationOri.distanceTo(locationDest);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public static String getCurrencyFormat(double money) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        return nf.format(money);
    }

    public static double getDoubleFromFormattedMoney(String strMoney) {
        String tempStr = strMoney.substring(0, strMoney.indexOf("₫") - 1);
        tempStr = tempStr.replaceAll("\\.", "");
        tempStr = tempStr.replaceAll(",", ".");
        tempStr = tempStr.trim();
        double money = Double.parseDouble(tempStr);

        return money;
    }
}
