package ua.comparis.javaclass.geo;

import lombok.experimental.UtilityClass;
import ua.comparis.javaclass.domain.DMStoDD;
import ua.comparis.javaclass.domain.SourceDMStoDD;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DMStoDDConverter {

    public static DMStoDD rezults (SourceDMStoDD source) {
        DMStoDD rezultDMStoDD = new DMStoDD();

        rezultDMStoDD.setLatD(source.getLatD());
        rezultDMStoDD.setLatM(source.getLatM());
        rezultDMStoDD.setLatS(source.getLatS());
        rezultDMStoDD.setLongD(source.getLongD());
        rezultDMStoDD.setLongM(source.getLongM());
        rezultDMStoDD.setLongS(source.getLongS());

        rezultDMStoDD.setLatitudeDD(Math.rint((Double.parseDouble(source.getLatD())+(Double.parseDouble(source.getLatM())/60)+(Double.parseDouble(source.getLatS())/60/60))*100000000)/100000000);
        rezultDMStoDD.setLongitudeDD(Math.rint((Double.parseDouble(source.getLongD())+(Double.parseDouble(source.getLongM())/60)+(Double.parseDouble(source.getLongS())/60/60))*100000000)/100000000);
        rezultDMStoDD.setAltitudeDD(Double.parseDouble(source.getAltitude()));

        return rezultDMStoDD;
    }

    public static List<DMStoDD> rezultDMStoDDBulk(List<SourceDMStoDD> sources) {
        return sources.stream().map(DMStoDDConverter::rezults).collect(Collectors.toList());
    }
}






