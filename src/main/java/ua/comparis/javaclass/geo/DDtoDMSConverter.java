package ua.comparis.javaclass.geo;

import lombok.experimental.UtilityClass;
import ua.comparis.javaclass.domain.DDtoDMS;
import ua.comparis.javaclass.domain.SourceDD;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.rint;

@UtilityClass
public class DDtoDMSConverter {
    int latD;
    int latM;
    double latS;
    int longD;
    int longM;
    double longS;

    public static DDtoDMS results( SourceDD source ) {
        DDtoDMS resultDDtoDMS = new DDtoDMS();

        latD = (int) abs(source.getLatD());
        latM = (int) (abs(source.getLatD() - latD) * 60);
        latS = rint((((source.getLatD() - latD) * 60) - latM) * 60 * 10000000) / 10000000;

        longD = (int) abs(source.getLongD());
        longM = (int) (abs(source.getLongD() - longD) * 60);
        longS = rint(((source.getLongD() - longD) * 60 - longM) * 60 * 10000000) / 10000000;

        resultDDtoDMS.setLatitudeDD(source.getLatD());
        resultDDtoDMS.setLongitudeDD(source.getLongD());

        resultDDtoDMS.setLatD(latD);
        resultDDtoDMS.setLatM(latM);
        resultDDtoDMS.setLatS(latS);
        resultDDtoDMS.setLongD(longD);
        resultDDtoDMS.setLongM(longM);
        resultDDtoDMS.setLongS(longS);

        resultDDtoDMS.setAltitudeDD(source.getAltitude());

        return resultDDtoDMS;
    }

    public static List<DDtoDMS> resultDDtoDMSBulk( List<SourceDD> sources ) {
        return sources.stream().map(DDtoDMSConverter::results).collect(Collectors.toList());
    }
}






