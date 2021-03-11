package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DMStoCK42 {
    private double latitudeDD;
    private double longitudeDD;
    private double altitudeDD;

    private int latD;
    private int latM;
    private double latS;
    private int longD;
    private int longM;
    private double longS;

    private double xCK42;
    private double yCK42;
    private double hCK42;
    private int zoneCK42;

    private String memo;

    @AllArgsConstructor
    @Data
    public static class DD {
        private double latitudeDD;
        private double longitudeDD;
        //private double altitudeDD;
    }

    @AllArgsConstructor
    @Data
    public static class DMS {
        private int latD;
        private int latM;
        private double latS;
        private int longD;
        private int longM;
        private double longS;
    }

    @AllArgsConstructor
    @Data
    public static class CK42 {
        private double xCK42;
        private double yCK42;
        private double hCK42;
        private double zoneCK42;
    }

    @Override
    public String toString() {
        return latD +"°"+latM+"'"+latS+"\",  "+ longD+"°"+longM+"'"+longS+"\"" + ", " + latitudeDD + ", " + longitudeDD + ", " + altitudeDD + ", " + xCK42 + ", " + yCK42 + ", " + hCK42 + ", " + memo+ "\n";
    }
}
