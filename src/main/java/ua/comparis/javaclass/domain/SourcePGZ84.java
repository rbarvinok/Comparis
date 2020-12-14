package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourcePGZ84 {

    private double latitude1;
    private double longitude1;
    private double altitude1;

    private double distance;
    private double angle;

    @Override
    public String toString() {
        return latitude1 + ",    " + longitude1 + ",    " + altitude1 + ",    " + distance + ",    " + angle + "\n";
    }


    public String toStringKML1() {
        return longitude1 + "," + latitude1 + "," + altitude1 + "\n";
    }
}

