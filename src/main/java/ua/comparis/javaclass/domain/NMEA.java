package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NMEA {

    private String timeDD;
    private double latitudeDD;
    private double longitudeDD;
    private double altitudeDD;

    public NMEA() { }

    @AllArgsConstructor
    @Data
    public static class DD {
        private double latitudeDD;
        private double longitudeDD;
    }
    @Override
    public String toString() {
        return  timeDD + ",      " + latitudeDD + ",     "  + longitudeDD +",      " + altitudeDD + "\n";
    }

}

