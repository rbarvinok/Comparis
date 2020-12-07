package ua.comparis.javaclass.geo;

import lombok.experimental.UtilityClass;
import ua.comparis.javaclass.domain.DDtoDMS;
import ua.comparis.javaclass.domain.SourceDDtoDMS;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.rint;

@UtilityClass
public class DDtoDMSConverter {
    private int latD;
    private int latM;
    private double latS;
    private int longD;
    private int longM;
    private double longS;

    public static DDtoDMS rezults (SourceDDtoDMS source) {
        DDtoDMS rezultDDtoDMS = new DDtoDMS();

        latD = (int) abs(source.getLatD());
        latM = (int) (abs(source.getLatD() - latD)*60);
        latS =  rint((((source.getLatD() - latD)*60) - latM)*60*10000000)/10000000;

        longD = (int) abs(source.getLongD());
        longM = (int) (abs(source.getLongD() - longD)*60);
        longS =  rint(((source.getLongD() - longD)*60 - longM)*60*10000000)/10000000;

        rezultDDtoDMS.setLatitudeDD(source.getLatD());
        rezultDDtoDMS.setLongitudeDD(source.getLongD());

        rezultDDtoDMS.setLatD((latD));
        rezultDDtoDMS.setLatM((latM));
        rezultDDtoDMS.setLatS(latS);
        rezultDDtoDMS.setLongD((longD));
        rezultDDtoDMS.setLongM((longM));
        rezultDDtoDMS.setLongS(longS);

        rezultDDtoDMS.setAltitudeDD(source.getAltitude());

        return rezultDDtoDMS;
    }

    public static List<DDtoDMS> rezultDDtoDMSBulk(List<SourceDDtoDMS> sources) {
        return sources.stream().map(DDtoDMSConverter::rezults).collect(Collectors.toList());
    }
}






