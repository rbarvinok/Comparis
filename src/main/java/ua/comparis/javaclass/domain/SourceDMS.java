package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourceDMS {

    private int latD;
    private int latM;
    private double latS;
    private int longD;
    private int longM;
    private double longS;
    private double altitude;
    private String memo;

    @Override
    public String toString() {
        return  latD +"°"+latM+"'"+latS+"\",  "+ longD+"°"+longM+"'"+longS+"\", " + altitude + ",  " + memo +"\n";
    }

}

