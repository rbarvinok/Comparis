package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourceDDtoDMS {

    private double latD;
    private double longD;
    private double altitude;


    @Override
    public String toString() {
        return  latD + ",      " + longD+ ",     "  + altitude + "\n";
    }

}

