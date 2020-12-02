package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DMStoDD {
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

    @Override
    public String toString() {
        return latitudeDD + ",    " + longitudeDD + ",    " + altitudeDD + "\n";
    }
}
