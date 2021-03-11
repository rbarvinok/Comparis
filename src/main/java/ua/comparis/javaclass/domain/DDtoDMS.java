package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DDtoDMS {
    private double latitudeDD;
    private double longitudeDD;
    private double altitudeDD;
    private String memo;

    private int latD;
    private int latM;
    private double latS;
    private int longD;
    private int longM;
    private double longS;


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
    @Override
    public String toString() {
        return latitudeDD + ", " + longitudeDD + ", " + latD +"°"+latM+"'"+latS+"\",  "+ longD+"°"+longM+"'"+longS+"\", " + altitudeDD + ", " + memo +"\n";
    }
}
