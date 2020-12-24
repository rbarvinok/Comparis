package ua.comparis.javaclass.geo;

import ua.comparis.controller.AccuracyController;
import ua.comparis.javaclass.domain.NMEA;
import ua.comparis.javaclass.domain.SourceDD;

import static java.lang.Math.abs;
import static ua.comparis.controller.AccuracyController.trueLat;
import static ua.comparis.controller.Controller.sourceDD;

public class AccuracyCalculator {

    AccuracyController accuracyController = new AccuracyController();


    public double lat;
    public double lon;
    public double alt;

    public void getCoordinates() {

        if (accuracyController.sourceType.equals("DD")) {
//            public static Accuracy results( SourceDD source ) {
//            List<String> source = sourceDD.stream().map(SourceDD::toString).collect(Collectors.toList());
            SourceDD source = new SourceDD();

            lat = source.getLatD();
            lon = source.getLongD();
            alt = source.getAltitude();

            System.out.println(sourceDD);
            System.out.println(lat + "  " +lon+ "  " + alt);
        }

        if (accuracyController.sourceType.equals("NMEA")) {
            NMEA source = new NMEA();
            lat = source.getLatitudeDD();
            lon = source.getLongitudeDD();
            alt = source.getAltitudeDD();

            System.out.println(lat + "  " + "  " + alt);
        }
    }


    public void accuracyLat() {
        getCoordinates();
        double deltaD = abs(lat - trueLat);
        //System.out.println(lat);

    }

    public void accuracyLong() {
    }

    public void accuracyAlt() {
    }

//    public static List<Accuracy> resultAccuracyBulk( List<SourceDD> sources ) {
//        return sources.stream().map(AccuracyCalculator::results).collect(Collectors.toList());
//    }
}






