package ua.comparis.javaclass.servisClass;

public class ConverterCoordinateSystem {

    private static final double aP = 6378245; // Большая полуось


    //Преобразование Гаус-Крюгер -> BLH в СК42

    double latitude42, longitude42, altitude42;

    public double getLatitude42() {
        return latitude42;
    }

    public double getLongitude42() {
        return longitude42;
    }

    public double getAltitude42() {
        return altitude42;
    }


    public void GKtoBLh(double x, double y, double h) {


        double n, l, b, z0, B0, DB;
        //n = Math.Truncate(y * 0.000001);
        //n = Math.floor(y * 0.000001);
        n = (int) (y * 0.000001);

        b = x / 6367558.4968;

        B0 = b + Math.sin(2 * b) * (0.00252588685 - 0.00001491860 * Math.pow(Math.sin(b), 2) + 0.00000011904 * Math.pow(Math.sin(b), 4));
        z0 = (y - (10 * n + 5) * 100000) / (aP * Math.cos(B0));

        l = z0 * (1 - 0.0033467108 * Math.pow(Math.sin(B0), 2) - 0.0000056002 * Math.pow(Math.sin(B0), 4) - 0.0000000187 * Math.pow(Math.sin(B0), 6) -
                Math.pow(z0, 2) * (0.16778975 + 0.16273586 * Math.pow(Math.sin(B0), 2) - 0.00052490 * Math.pow(Math.sin(B0), 4) - 0.00000846 * Math.pow(Math.sin(B0), 6) -
                        Math.pow(z0, 2) * (0.0420025 + 0.1487407 * Math.pow(Math.sin(B0), 2) + 0.0059420 * Math.pow(Math.sin(B0), 4) - 0.0000150 * Math.pow(Math.sin(B0), 6) -
                                Math.pow(z0, 2) * (0.01225 + 0.09477 * Math.pow(Math.sin(B0), 2) + 0.03282 * Math.pow(Math.sin(B0), 4) - 0.00034 * Math.pow(Math.sin(B0), 6) -
                                        Math.pow(z0, 2) * (0.0038 + 0.0524 * Math.pow(Math.sin(B0), 2) + 0.0482 * Math.pow(Math.sin(B0), 4) + 0.0032 * Math.pow(Math.sin(B0), 6))))));


        DB = -Math.pow(z0, 2) * Math.sin(2 * B0) * (0.251684631 - 0.003369263 * Math.pow(Math.sin(B0), 2) + 0.000011276 * Math.pow(Math.sin(B0), 4) -    //  *  Math.Pow(Math.Sin(B0),2)
                Math.pow(z0, 2) * (0.10500614 - 0.04559916 * Math.pow(Math.sin(B0), 2) + 0.00228901 * Math.pow(Math.sin(B0), 4) - 0.00002987 * Math.pow(Math.sin(B0), 6) -
                        Math.pow(z0, 2) * (0.042858 - 0.025318 * Math.pow(Math.sin(B0), 2) + 0.014346 * Math.pow(Math.sin(B0), 4) - 0.001264 * Math.pow(Math.sin(B0), 6) -
                                Math.pow(z0, 2) * (0.01672 - 0.00630 * Math.pow(Math.sin(B0), 2) + 0.01188 * Math.pow(Math.sin(B0), 4) - 0.00328 * Math.pow(Math.sin(B0), 6)))));

        latitude42 = B0 + DB;
        longitude42 = 6 * (n - 0.5) / 57.29577951 + l;
        altitude42 = h;
    }

//------------------------------------------------------------------------------------------------------------------------------------------------

    //преобразование СК42 BLH в Гаус-Крюгер
    private double GK_x;
    private double GK_y;
    private double n;


    public Double getGK_x() {
        return Math.rint(GK_x * 100) / 100;
    }

    public Double getGK_y() {
        return Math.rint(GK_y * 100) / 100;
    }

    public Double getN() {
        return n;
    }

    public void BLHtoGK(double latitude, double longitude, double altitude) {
        calculateGK_x(latitude, longitude, altitude);
        calculateGK_y(latitude, longitude, altitude);
        calculateN(longitude);
    }

    private void calculateGK_x(double B, double L, double h) {
        double l = (((L * 180) / Math.PI) - (3 + 6 * (n - 1))) / 57.29577951;
        GK_x = (6367558.4968 * B) - Math.sin(2 * B) * (16002.8900 + 66.9607 * Math.pow(Math.sin(B), 2) + 0.3515 * Math.pow(Math.sin(B), 4) -  // (6367558.4968 * B) - 2 * Math.Sin(B) *
                Math.pow(l, 2) * (1594561.25 + 5336.535 * Math.pow(Math.sin(B), 2) + 26.790 * Math.pow(Math.sin(B), 4) + 0.149 * Math.pow(Math.sin(B), 6) +
                        Math.pow(l, 2) * (672483.4 - 811219.9 * Math.pow(Math.sin(B), 2) + 5420.0 * Math.pow(Math.sin(B), 4) - 10.6 * Math.pow(Math.sin(B), 6) +
                                Math.pow(l, 2) * (278194 - 830174 * Math.pow(Math.sin(B), 2) + 572434 * Math.pow(Math.sin(B), 4) - 16010 * Math.pow(Math.sin(B), 6) +
                                        Math.pow(l, 2) * (109500 - 574700 * Math.pow(Math.sin(B), 2) + 863700 * Math.pow(Math.sin(B), 4) - 398600 * Math.pow(Math.sin(B), 6))))));
    }

    private void calculateGK_y(double B, double L, double h) {
        double l;
        l = (((L * 180) / Math.PI) - (3 + (6 * (n - 1)))) / 57.29577951;
        GK_y = (5 + 10 * n) * 100000 + l * Math.cos(B) * (6378245 + 21346.1415 * Math.pow(Math.sin(B), 2) + 107.1590 * Math.pow(Math.sin(B), 4) +
                0.5977 * Math.pow(Math.sin(B), 6) + Math.pow(l, 2) * (1070204.16 - 2136826.66 * Math.pow(Math.sin(B), 2) + 17.98 * Math.pow(Math.sin(B), 4) - 11.99 * Math.pow(Math.sin(B), 6) +
                Math.pow(l, 2) * (270806 - 1523417 * Math.pow(Math.sin(B), 2) + 1327645 * Math.pow(Math.sin(B), 4) - 21701 * Math.pow(Math.sin(B), 6) +
                        Math.pow(l, 2) * (79690 - 866190 * Math.pow(Math.sin(B), 2) + 1730360 * Math.pow(Math.sin(B), 4) - 945460 * Math.pow(Math.sin(B), 6)))));
    }

    private void calculateN(double L) {//номер шестиградусной зоны в проекции Гаусса-Крюгера
        n = Math.floor((6 + (L * 180 / Math.PI)) / 6);
    }


}