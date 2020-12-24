package ua.comparis.javaclass.geo;

import ua.comparis.javaclass.domain.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.Math.rint;

public class ConverterCoordinateSystem {

    private static final double aP = 6378245; // Большая полуось


    //Преобразование Гаус-Крюгер -> BLH в СК42

    static double latitude42;
    static double longitude42;
    static double altitude42;

    public double getLatitude42() {
        return latitude42;
    }

    public double getLongitude42() {
        return longitude42;
    }

    public double getAltitude42() {
        return altitude42;
    }


    public static void GKtoBLh( double x, double y, double h ) {


        double n, l, b, z0, B0, DB;
        n = (int) (y * 0.000001);

        b = x / 6367558.4968;

        B0 = b + sin(2 * b) * (0.00252588685 - 0.00001491860 * pow(sin(b), 2) + 0.00000011904 * pow(sin(b), 4));
        z0 = (y - (10 * n + 5) * 100000) / (aP * cos(B0));

        l = z0 * (1 - 0.0033467108 * pow(sin(B0), 2) - 0.0000056002 * pow(sin(B0), 4) - 0.0000000187 * pow(sin(B0), 6) -
                pow(z0, 2) * (0.16778975 + 0.16273586 * pow(sin(B0), 2) - 0.00052490 * pow(sin(B0), 4) - 0.00000846 * pow(sin(B0), 6) -
                        pow(z0, 2) * (0.0420025 + 0.1487407 * pow(sin(B0), 2) + 0.0059420 * pow(sin(B0), 4) - 0.0000150 * pow(sin(B0), 6) -
                                pow(z0, 2) * (0.01225 + 0.09477 * pow(sin(B0), 2) + 0.03282 * pow(sin(B0), 4) - 0.00034 * pow(sin(B0), 6) -
                                        pow(z0, 2) * (0.0038 + 0.0524 * pow(sin(B0), 2) + 0.0482 * pow(sin(B0), 4) + 0.0032 * pow(sin(B0), 6))))));


        DB = -pow(z0, 2) * sin(2 * B0) * (0.251684631 - 0.003369263 * pow(sin(B0), 2) + 0.000011276 * pow(sin(B0), 4) -    //  *  Math.Pow(Math.Sin(B0),2)
                pow(z0, 2) * (0.10500614 - 0.04559916 * pow(sin(B0), 2) + 0.00228901 * pow(sin(B0), 4) - 0.00002987 * pow(sin(B0), 6) -
                        pow(z0, 2) * (0.042858 - 0.025318 * pow(sin(B0), 2) + 0.014346 * pow(sin(B0), 4) - 0.001264 * pow(sin(B0), 6) -
                                pow(z0, 2) * (0.01672 - 0.00630 * pow(sin(B0), 2) + 0.01188 * pow(sin(B0), 4) - 0.00328 * pow(sin(B0), 6)))));

        latitude42 = B0 + DB;
        longitude42 = 6 * (n - 0.5) / 57.29577951 + l;
        altitude42 = h;
        //System.out.println(Math.toDegrees(latitude42) + "     " + Math.toDegrees(longitude42) + "     " + altitude42);
    }

//------------------------------------------------------------------------------------------------------------------------------------------------

    //преобразование СК42 BLH в Гаус-Крюгер
    private static double GK_x;
    public static double GK_y;
    private static double n;


    public Double getGK_x() {
        return rint(GK_x * 100) / 100;
    }

    public Double getGK_y() {
        return rint(GK_y * 100) / 100;
    }

    public Double getN() {
        return n;
    }

    public static void BLHtoGK( double latitude, double longitude, double altitude ) {
        calculateGK_x(latitude, longitude, altitude);
        calculateGK_y(latitude, longitude, altitude);
        calculateN(longitude);
    }

    private static void calculateN( double L ) {//номер шестиградусной зоны в проекции Гаусса-Крюгера
        n = (int) ((6 + (L * 180 / PI)) / 6);
    }

    private static void calculateGK_x( double B, double L, double h ) {
        double l = ((L * 180 / PI) - (3 + 6 * (n - 1))) / 57.29577951;
        GK_x = (6367558.4968 * B) - sin(2 * B) * (16002.8900 + 66.9607 * pow(sin(B), 2) + 0.3515 * pow(sin(B), 4) -  // (6367558.4968 * B) - 2 * Math.Sin(B) *
                pow(l, 2) * (1594561.25 + 5336.535 *
                        pow(sin(B), 2) + 26.790 *
                        pow(sin(B), 4) + 0.149 * pow(sin(B), 6) +
                        pow(l, 2) * (672483.4 - 811219.9 *
                                pow(sin(B), 2) + 5420.0 *
                                pow(sin(B), 4) - 10.6 * pow(sin(B), 6) +
                                pow(l, 2) * (278194 - 830174 *
                                        pow(sin(B), 2) + 572434 *
                                        pow(sin(B), 4) - 16010 * pow(sin(B), 6) +
                                        pow(l, 2) * (109500 - 574700 *
                                                pow(sin(B), 2) + 863700 *
                                                pow(sin(B), 4) - 398600 *
                                                pow(sin(B), 6))))));
    }

    private static void calculateGK_y( double B, double L, double h ) {
        double l;
        l = ((L * 180 / PI) - (3 + (6 * (n - 1)))) / 57.29577951;
        GK_y = (5 + 10 * n) * 100000 + l * cos(B) * (6378245 + 21346.1415 * pow(sin(B), 2) + 107.1590 * pow(sin(B), 4) +
                0.5977 * pow(sin(B), 6) + pow(l, 2) * (1070204.16 - 2136826.66 * pow(sin(B), 2) + 17.98 * pow(sin(B), 4) - 11.99 * pow(sin(B), 6) +
                pow(l, 2) * (270806 - 1523417 * pow(sin(B), 2) + 1327645 * pow(sin(B), 4) - 21701 * pow(sin(B), 6) +
                        pow(l, 2) * (79690 - 866190 * pow(sin(B), 2) + 1730360 * pow(sin(B), 4) - 945460 * pow(sin(B), 6)))));
    }

//-----------------------------------------------------------------

    public static DDtoCK42 rezultsCK42( SourceDD source ) {

        int latD = (int) abs(source.getLatD());
        int latM = (int) (abs(source.getLatD() - latD) * 60);
        double latS = rint((((source.getLatD() - latD) * 60) - latM) * 60 * 10000000) / 10000000;

        int longD = (int) abs(source.getLongD());
        int longM = (int) (abs(source.getLongD() - longD) * 60);
        double longS = rint(((source.getLongD() - longD) * 60 - longM) * 60 * 10000000) / 10000000;

        ConverterCoordinates wgs84toCk42 = new ConverterCoordinates();
        wgs84toCk42.Wgs84ToCk42Converter(source.getLatD(), source.getLongD(), source.getAltitude());

        BLHtoGK(wgs84toCk42.getLatitude42(), wgs84toCk42.getLongitude42(), wgs84toCk42.getAltitude42());

        DDtoCK42 rezultDDtoCK42 = new DDtoCK42();

        rezultDDtoCK42.setLatitudeDD(source.getLatD());
        rezultDDtoCK42.setLongitudeDD(source.getLongD());
        rezultDDtoCK42.setAltitudeDD(source.getAltitude());

        rezultDDtoCK42.setLatD(latD);
        rezultDDtoCK42.setLatM(latM);
        rezultDDtoCK42.setLatS(latS);
        rezultDDtoCK42.setLongD(longD);
        rezultDDtoCK42.setLongM(longM);
        rezultDDtoCK42.setLongS(longS);

        rezultDDtoCK42.setXCK42(GK_x);
        rezultDDtoCK42.setYCK42(GK_y);
        rezultDDtoCK42.setHCK42(wgs84toCk42.getAltitude42());
        rezultDDtoCK42.setZoneCK42((int) n);
        return rezultDDtoCK42;
    }

    public static List<DDtoCK42> rezultDDtoCK42Bulk( List<SourceDD> sources ) {
        return sources.stream().map(ConverterCoordinateSystem::rezultsCK42).collect(Collectors.toList());
    }

    //-----------------------------------------------------------------

    public static DMStoCK42 rezCK42( SourceDMS source ) {

        double latitudeDD = (Math.rint((source.getLatD() + Double.valueOf(source.getLatM()) / 60 + Double.valueOf(source.getLatS()) / 60 / 60) * 100000000) / 100000000);
        double longitudeDD = (Math.rint((source.getLongD() + Double.valueOf(source.getLongM()) / 60 + Double.valueOf(source.getLongS()) / 60 / 60) * 100000000) / 100000000);

        ConverterCoordinates wgs84toCk42 = new ConverterCoordinates();
        wgs84toCk42.Wgs84ToCk42Converter(latitudeDD, longitudeDD, source.getAltitude());

        BLHtoGK(wgs84toCk42.getLatitude42(), wgs84toCk42.getLongitude42(), wgs84toCk42.getAltitude42());

        DMStoCK42 rezultDMStoCK42 = new DMStoCK42();

        rezultDMStoCK42.setLatitudeDD(latitudeDD);
        rezultDMStoCK42.setLongitudeDD(longitudeDD);

        rezultDMStoCK42.setAltitudeDD(source.getAltitude());

        rezultDMStoCK42.setLatD(source.getLatD());
        rezultDMStoCK42.setLatM(source.getLatM());
        rezultDMStoCK42.setLatS(source.getLatS());
        rezultDMStoCK42.setLongD(source.getLongD());
        rezultDMStoCK42.setLongM(source.getLongM());
        rezultDMStoCK42.setLongS(source.getLongS());

        rezultDMStoCK42.setXCK42(GK_x);
        rezultDMStoCK42.setYCK42(GK_y);
        rezultDMStoCK42.setHCK42(wgs84toCk42.getAltitude42());
        rezultDMStoCK42.setZoneCK42((int) n);
        return rezultDMStoCK42;
    }

    public static List<DMStoCK42> rezultDMStoCK42Bulk( List<SourceDMS> sources ) {
        return sources.stream().map(ConverterCoordinateSystem::rezCK42).collect(Collectors.toList());
    }


//    ------------------------------------------------------------

    public static CK42toDD rezultsCK42toDD( SourceDD source ) {

        GKtoBLh(source.getLatD(), source.getLongD(), source.getAltitude());

        ConverterCoordinates CK42toWGS84 = new ConverterCoordinates();
        CK42toWGS84.Ck42ToWgs84Converter(Math.toDegrees(latitude42), Math.toDegrees(longitude42), altitude42);

        int latD = (int) abs(CK42toWGS84.getLatitude84());
        int latM = (int) (abs(CK42toWGS84.getLatitude84() - latD) * 60);
        double latS = rint((((CK42toWGS84.getLatitude84() - latD) * 60) - latM) * 60 * 10000000) / 10000000;

        int longD = (int) abs(CK42toWGS84.getLongitude84());
        int longM = (int) (abs(CK42toWGS84.getLongitude84() - longD) * 60);
        double longS = rint(((CK42toWGS84.getLongitude84() - longD) * 60 - longM) * 60 * 10000000) / 10000000;

        CK42toDD rezultCK42toDD = new CK42toDD();
        rezultCK42toDD.setXCK42(source.getLatD());
        rezultCK42toDD.setYCK42(source.getLongD());
        rezultCK42toDD.setHCK42(source.getAltitude());

        rezultCK42toDD.setLatitudeDD(CK42toWGS84.getLatitude84());
        rezultCK42toDD.setLongitudeDD(CK42toWGS84.getLongitude84());
        rezultCK42toDD.setAltitudeDD(CK42toWGS84.getAltitude84());

        rezultCK42toDD.setLatD(latD);
        rezultCK42toDD.setLatM(latM);
        rezultCK42toDD.setLatS(latS);
        rezultCK42toDD.setLongD(longD);
        rezultCK42toDD.setLongM(longM);
        rezultCK42toDD.setLongS(longS);
        return rezultCK42toDD;
    }

    public static List<CK42toDD> rezultCK42toDDBulk( List<SourceDD> sources ) {
        return sources.stream().map(ConverterCoordinateSystem::rezultsCK42toDD).collect(Collectors.toList());
    }
}