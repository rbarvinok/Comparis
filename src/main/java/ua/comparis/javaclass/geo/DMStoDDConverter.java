package ua.comparis.javaclass.geo;

import lombok.experimental.UtilityClass;
import ua.comparis.javaclass.domain.DMStoDD;
import ua.comparis.javaclass.domain.SourceDMS;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DMStoDDConverter {

    public static DMStoDD rezults (SourceDMS source) {
        DMStoDD rezultDMStoDD = new DMStoDD();

        rezultDMStoDD.setLatD(source.getLatD());
        rezultDMStoDD.setLatM(source.getLatM());
        rezultDMStoDD.setLatS(source.getLatS());
        rezultDMStoDD.setLongD(source.getLongD());
        rezultDMStoDD.setLongM(source.getLongM());
        rezultDMStoDD.setLongS(source.getLongS());

        rezultDMStoDD.setLatitudeDD(Math.rint((source.getLatD()+Double.valueOf(source.getLatM())/60+Double.valueOf(source.getLatS())/60/60)*100000000)/100000000);
        rezultDMStoDD.setLongitudeDD(Math.rint((source.getLongD()+Double.valueOf(source.getLongM())/60+Double.valueOf(source.getLongS())/60/60)*100000000)/100000000);
        rezultDMStoDD.setAltitudeDD(source.getAltitude());

        return rezultDMStoDD;
    }

    public static List<DMStoDD> rezultDMStoDDBulk(List<SourceDMS> sources) {
        return sources.stream().map(DMStoDDConverter::rezults).collect(Collectors.toList());
    }
}






