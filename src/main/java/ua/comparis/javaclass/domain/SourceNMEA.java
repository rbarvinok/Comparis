package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourceNMEA {

    private String timeNMEA;
    private String latNMEA;
    private String longNMEA;
    private double altNmea;

    public SourceNMEA() {

    }


    @Override
    public String toString() {
        return  timeNMEA + ",      " + latNMEA+ ",     "  + longNMEA +",      " + altNmea + "\n";
    }

}

