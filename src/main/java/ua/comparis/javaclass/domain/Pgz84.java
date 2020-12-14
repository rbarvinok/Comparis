package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Pgz84 {

    private double latitude1;
    private double longitude1;
    private double altitude1;

    private double distance;
    private double angle;

    private double latitude2;
    private double longitude2;
    //private double altitude2;

    @AllArgsConstructor
    @Data
    public static class Latitude1 {
        private double latitude1;
        private double longitude1;
    }


    @AllArgsConstructor
    @Data
    public static class Latitude2 {
        private double latitude2;
        private double longitude2;
    }

    @Override
    public String toString() {
        return latitude1 + ",    " + longitude1 + ",    " + altitude1 + ",        "  +
                distance +  ",      " + angle + ",      " +
                latitude2 + ",    " + longitude2 + "\n";
    }

//    public String toStringKML1() {
//        return longitude1 + "," + latitude1 + "," + altitude1  + "\n";
//    }
//    public String toStringKML2() {
//        return longitude2 + "," + latitude2 + "," + altitude2  + "\n";
//    }
}

