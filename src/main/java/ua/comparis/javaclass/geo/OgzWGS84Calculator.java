package ua.comparis.javaclass.geo;

import lombok.experimental.UtilityClass;
import ua.comparis.javaclass.domain.Ogz84;
import ua.comparis.javaclass.domain.SourceOGZ84;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.StrictMath.*;

@UtilityClass
public class OgzWGS84Calculator {

    public static Ogz84 rezults( SourceOGZ84 source ) {
        Ogz84 rezultOGZ84 = new Ogz84();

        double dist;
        double dist3D;
        double deltaH;
        double ang1 = 0;
        double ang2;


        double lat1 = Double.parseDouble(source.getLatitude1()) * PI / 180;
        double long1 = Double.parseDouble(source.getLongitude1()) * PI / 180;
        double lat2 = Double.parseDouble(source.getLatitude2()) * PI / 180;
        double long2 = Double.parseDouble(source.getLongitude2()) * PI / 180;
        double alt1 = Double.parseDouble(source.getAltitude1());
        double alt2 = Double.parseDouble(source.getAltitude2());

        double a = 6378137; //Радиус на экваторе WGS84
        double f = 1 / 298.257223563; //Сжатие WGS84
        double b = (1 - f) * a; //Радиус на полюсах
        double U1 = atan((1 - f) * tan(lat1));
        double U2 = atan((1 - f) * tan(lat2));
        double L = long2 - long1;
        double cosU1 = cos(U1);
        double cosU2 = cos(U2);
        double sinU1 = sin(U1);
        double sinU2 = sin(U2);
        double y = L;
        double y_, sinq, cosq, q, sina, cos2a, cos2qm, cosy, siny, C;
        do {
            cosy = cos(y);
            siny = sin(y);
            //sinq = Sqrt(Pow((cosU2 * siny), 2) + Pow((cosU1 * sinU2 - sinU1 * cosU2 * cosy), 2));
            double sinq2 = pow((cosU2 * siny), 2) + pow((cosU1 * sinU2 - sinU1 * cosU2 * cosy), 2);
            sinq = sqrt(sinq2);
            cosq = sinU1 * sinU2 + cosU1 * cosU2 * cosy;
            //q = Atan(sinq / cosq);
            Double tanq = sinq / cosq; ///
            if (tanq > 0) q = atan(tanq);
            else q = atan(tanq) + PI;
            if (sinq2 == 0) sina = 0;
            else sina = (cosU1 * cosU2 * siny) / sinq;
            cos2a = 1 - pow(sina, 2);
            if (cos(asin(sina)) * cos(asin(sina)) == 0) cos2qm = 0;
            else cos2qm = cosq - (2 * sinU1 * sinU2) / cos2a;
            //sina = (cosU1 * cosU2 * siny) / sinq;
            // cos2qm = cosq - (2 * sinU1 * sinU2) / cos2a;
            C = f / 16 * cos2a * (4 + f * (4 - 3 * cos2a));
            y_ = y;
            y = L + (1 - C) * f * sina * (q + C * sinq * (cos2qm + C * cosq * (-1 + 2 * pow(cos2qm, 2))));
        } while (abs(y - y_) > 0.000000000001);

        double u2 = cos2a * (pow(a, 2) - pow(b, 2)) / pow(b, 2);
        double A = 1 + u2 / 16384 * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));
        double B = u2 / 1024 * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));
        double dq = B * sinq * (cos2qm + B / 4 * (cosq * (-1 + 2 * cos2qm) - B / 6 * cos2qm * (-3 + 4 * pow(sinq, 2)) * (-3 + 4 * pow(cos2qm, 2))));

        dist = b * A * (q - dq);
        dist3D = sqrt(pow(dist, 2) + pow((alt1 - alt2), 2));
        deltaH = alt1 - alt2;

        double a1 = atan(cosU2 * siny / (cosU1 * sinU2 - sinU1 * cosU2 * cosy));
        double dl = lat2 - lat1;

        if ((L >= 0) && (dl >= 0)) ang1 = a1 * 180 / PI;
        if ((L >= 0) && (dl < 0)) ang1 = ((PI) + a1) * 180 / PI;
        if ((L < 0) && (dl < 0)) ang1 = (a1 + PI) * 180 / PI;
        if ((L < 0) && (dl > 0)) ang1 = (a1 + PI * 2) * 180 / PI;

        ang2 = (atan(cosU1 * siny / (-sinU1 * cosU2 - cosU1 * sinU2 * cosy))) * 180 / PI;


        rezultOGZ84.setLocalTime1(source.getLocalTime1());
        rezultOGZ84.setLatitude1(source.getLatitude1());
        rezultOGZ84.setLongitude1(source.getLongitude1());
        rezultOGZ84.setAltitude1(source.getAltitude1());

        rezultOGZ84.setLocalTime2(source.getLocalTime2());
        rezultOGZ84.setLatitude2(source.getLatitude2());
        rezultOGZ84.setLongitude2(source.getLongitude2());
        rezultOGZ84.setAltitude2(source.getAltitude2());

        rezultOGZ84.setDistance2D(rint(dist * 1000) / 1000);
        rezultOGZ84.setDistance3D(rint(dist3D * 1000) / 1000);
        rezultOGZ84.setAngle(rint(ang1 * 100) / 100);
        rezultOGZ84.setDeltaH(rint(deltaH * 1000) / 1000);
        return rezultOGZ84;
    }

    public static List<Ogz84> rezultOGZ84Bulk( List<SourceOGZ84> sources ) {
        return sources.stream().map(OgzWGS84Calculator::rezults).collect(Collectors.toList());
    }
}






