package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourceOGZ84 {

    private String localTime1;
    private String latitude1;
    private String longitude1;
    private String altitude1;

    private String localTime2;
    private String latitude2;
    private String longitude2;
    private String altitude2;


    @Override
    public String toString() {
        return  localTime1 + ",    " +latitude1 + ",    " + longitude1 + ",    " + altitude1 + ",    "  + localTime2 + ",    " +latitude2 + ",    " + longitude2 + ",    " + altitude2  + "\n";
    }


    public String toStringKML1() {
        return longitude1 + "," + latitude1 + "," + altitude1  + "\n";
    }

    public String toStringKML2() {
        return longitude2 + "," + latitude2 + "," + altitude2  + "\n";
    }

}

