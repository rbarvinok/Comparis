package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Rezult {

    private String localTime1;
    private double latitude1;
    private double longitude1;
    private double altitude1;

    private String localTime2;
    private double latitude2;
    private double longitude2;
    private double altitude2;

    private double distance2D;
    private double distance3D;
    private double angle;


    @AllArgsConstructor
    @Data
    public static class LocalTime1 {
        private String localTime1;
    }


    @AllArgsConstructor
    @Data
    public static class Latitude1 {
        private double latitude1;
    }

    @AllArgsConstructor
    @Data
    public static class Longitude1 {
        private double longitude1;
    }

    @AllArgsConstructor
    @Data
    public static class Altitude1 {
        private String time1;
        private double altitude1;
    }

    //////////////////////////////
    @AllArgsConstructor
    @Data
    public static class LocalTime2 {
        private String localTime2;
    }

    @AllArgsConstructor
    @Data
    public static class Latitude2 {
        private double latitude2;
        private double longitude2;
    }

    @AllArgsConstructor
    @Data
    public static class Longitude2 {
        private double longitude2;
    }

    @AllArgsConstructor
    @Data
    public static class Altitude2 {
        private String time2;
        private double altitude2;
    }



    @Override
    public String toString() {
        return localTime1 + ",    " +latitude1 + ",    " + longitude1 + ",    " + altitude1 + ",        "  +
                localTime2 + ",    " +latitude2 + ",    " + longitude2 + ",    " + altitude2  + ",         " +
                distance2D + ",    " + distance3D + ",      " + angle + "\n";
    }

    public String toStringKML1() {
        return longitude1 + "," + latitude1 + "," + altitude1  + "\n";
    }
    public String toStringKML2() {
        return longitude2 + "," + latitude2 + "," + altitude2  + "\n";
    }
}

