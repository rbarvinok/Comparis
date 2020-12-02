package ua.comparis.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourceDMStoDD {

    private String latD;
    private String latM;
    private String latS;
    private String longD;
    private String longM;
    private String longS;
    private String altitude;


    @Override
    public String toString() {
        return  latD + ",  " + latM + ",  " + latS + ",            " + longD+ ",  " + longM +",  " +  longS +",            " + altitude + "\n";
    }

}

