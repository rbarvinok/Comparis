package ua.comparis.javaclass.geo;

import lombok.experimental.UtilityClass;
import ua.comparis.javaclass.domain.NMEA;
import ua.comparis.javaclass.domain.SourceNMEA;

import java.util.List;
import java.util.stream.Collectors;

import static ua.comparis.controller.Controller.localZone;

@UtilityClass
public class NMEAParser {
    String time;
    double latD;
    double longD;

    public static NMEA rezults( SourceNMEA source ) {
        NMEA resultNMEA = new NMEA();

        time = Integer.parseInt(source.getTimeNMEA().substring(0,2)) + localZone +":" + source.getTimeNMEA().substring(2,4) + ":" + source.getTimeNMEA().substring(4);
        latD = Double.parseDouble(source.getLatNMEA().substring(0,2)) + Double.parseDouble(source.getLatNMEA().substring(2))/60;
        longD = Double.parseDouble(source.getLongNMEA().substring(0,3)) + Double.parseDouble(source.getLongNMEA().substring(3))/60;

        resultNMEA.setTimeDD(time);
        resultNMEA.setLatitudeDD(latD);
        resultNMEA.setLongitudeDD(longD);
        resultNMEA.setAltitudeDD(source.getAltNmea());

        return resultNMEA;
    }

    public static List<NMEA> resultNMEABulk( List<SourceNMEA> sources ) {
        return sources.stream().map(NMEAParser::rezults).collect(Collectors.toList());
    }
}






