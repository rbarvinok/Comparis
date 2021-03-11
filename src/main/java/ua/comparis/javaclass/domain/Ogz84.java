package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Ogz84 {

    private String localTime1;
    private String latitude1;
    private String longitude1;
    private String altitude1;

    private String localTime2;
    private String latitude2;
    private String longitude2;
    private String altitude2;

    private double distance2D;
    private double distance3D;
    private double angle;
    private double deltaH;

    @AllArgsConstructor
    @Data
    public static class Latitude1 {
        private double latitude1;
        private double longitude1;
    }


    @AllArgsConstructor
    @Data
    public static class Altitude1 {
        private String localTime1;
        private double altitude1;
    }


    @AllArgsConstructor
    @Data
    public static class Latitude2 {
        private double latitude2;
        private double longitude2;
    }


    @AllArgsConstructor
    @Data
    public static class Altitude2 {
        private String localTime2;
        private double altitude2;
    }



    @Override
    public String toString() {
        return localTime1 + ",    " +latitude1 + ",    " +longitude1 + ",    " + altitude1 + ",        "  +
                localTime2 + ",    " +latitude2 + ",    " +longitude2 + ",    " + altitude2  + ",         " +
                distance2D + ",    " + distance3D + ",      " +angle + ",      " + deltaH +"\n";
    }
}

