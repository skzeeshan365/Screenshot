package com.reiserx.screenshot.Utils;

import static com.reiserx.screenshot.Utils.FusedLocation.geolocateCoordinates;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ExifMetadata {
    public static String readLocationMetadata(String imagePath, Context context) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            // Read GPS latitude and longitude
            double latitude = convertRationalLatLonToDouble(
                    exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                    exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
            double longitude = convertRationalLatLonToDouble(
                    exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),
                    exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));

            if (latitude != 0 && longitude != 0) {
                return geolocateCoordinates(latitude, longitude, context);
            } else {
                return null;
            }

        } catch (IOException e) {
            return null;
        }
    }

    private static double convertRationalLatLonToDouble(String rationalString, String ref) {
        if (rationalString == null || ref == null) return 0.0;

        String[] parts = rationalString.split(",");
        if (parts.length != 3) return 0.0;

        try {
            double degrees = rationalToDouble(parts[0]);
            double minutes = rationalToDouble(parts[1]);
            double seconds = rationalToDouble(parts[2]);

            double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
            return (ref.equals("S") || ref.equals("W")) ? -result : result;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static double rationalToDouble(String rational) {
        String[] fraction = rational.split("/");
        if (fraction.length != 2) return 0.0;

        try {
            double numerator = Double.parseDouble(fraction[0]);
            double denominator = Double.parseDouble(fraction[1]);
            return numerator / denominator;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
