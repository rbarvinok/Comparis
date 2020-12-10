package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DMStoDD {
    private int latD;
    private int latM;
    private double latS;
    private int longD;
    private int longM;
    private double longS;

    private double latitudeDD;
    private double longitudeDD;
    private double altitudeDD;

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
        private double latD;
        private double latM;
        private double latS;
        private double longD;
        private double longM;
        private double longS;
    }
    @Override
    public String toString() {
        return latD +"°"+latM+"'"+latS+"\",          "+ longD+"°"+longM+"'"+longS+"\",          " + latitudeDD + ",    " + longitudeDD + ",        " + altitudeDD + "\n";
    }
}
