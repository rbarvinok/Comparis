package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Source {

    private String localTime1;
    private double latitude1;
    private double longitude1;
    private double altitude1;

    private String localTime2;
    private double latitude2;
    private double longitude2;
    private double altitude2;


//    @AllArgsConstructor
//    @Data
//    public static class LocalTime1 {
//        private String localTime1;
//    }
//
//
//    @AllArgsConstructor
//    @Data
//    public static class Latitude1 {
//        private double latitude1;
//           }
//
//    @AllArgsConstructor
//    @Data
//    public static class Longitude1 {
//        private double longitude1;
//    }
//
//    @AllArgsConstructor
//    @Data
//    public static class Altitude1 {
//        private String time1;
//        private double altitude1;
//    }
//
////////////////////////////////
//
//    @AllArgsConstructor
//    @Data
//    public static class LocalTime2 {
//        private String localTime2;
//    }
//
//    @AllArgsConstructor
//    @Data
//    public static class Latitude2 {
//        private double latitude2;
//        private double longitude2;
//    }
//
//    @AllArgsConstructor
//    @Data
//    public static class Longitude2 {
//        private double longitude2;
//    }
//
//    @AllArgsConstructor
//    @Data
//    public static class Altitude2 {
//        private String time2;
//        private double altitude2;
//    }
//
//

    @Override
    public String toString() {
        return  localTime1 + ",    " +latitude1 + ",    " + longitude1 + ",    " + altitude1 + ",    "  + localTime2 + ",    " +latitude2 + ",    " + longitude2 + ",    " + altitude2  + "\n";
    }


    public String toStringKML() {
        return longitude1 + "," + latitude1 + "," + altitude1  + "," + longitude2 + "," + latitude2 + "," + altitude2  + "\n";
    }


}

