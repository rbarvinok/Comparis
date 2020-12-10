package ua.comparis.javaclass.geo;

public class ConverterCoordinates {
    private static final double Pi = 3.14159265358979; // Число Пи
    private static final double ro = 206264.8062; // Число угловых секунд в радиане
    // Эллипсоид Красовского (Пулково 1942)
    private static final double aP = 6378245; // Большая полуось
    private static final double alP = 1 / 298.3; // Сжатие
    private static final double e2P = 2 * alP - alP * alP; // Квадрат эксцентриситета
    // Эллипсоид GRS80 (WGS84)
    private static final double aW = 6378137; // Большая полуось
    private static final double alW = 1 / 298.257223563; // Сжатие
    private static final double e2W = 2 * alW - alW * alW; // Квадрат эксцентриситета
    // Вспомогательные значения для преобразования эллипсоидов
    //1 - wgs->42
    private static final double a = (aP + aW) / 2; //Большая полуось(среднее арифметическое)
    private static final double e2 = (e2P + e2W) / 2; // Квадрат эксцентриситета (среднее арифметическое)
    private static final double da = aW - aP; //Разница между полуосями
    private static final double de2 = e2W - e2P; //Разница между эксцентриситетами
    //Линейные элементы трансформирования 42->84, в метрах
    private static final double dx = 23.92;
    private static final double dy = -141.27;
    private static final double dz = -80.9;
    // Угловые элементы трансформирования, в секундах
    private static final double wx = (double) 0;
    private static final double wy = -0.35;
    private static final double wz = -0.82;
    // Дифференциальное различие масштабов
    private static final double ms = -0.00000012;
    // Угловые элементы трансформирования, в секундах
    private static final double wx9084 = 0;
    private static final double wy9084 = 0;
    private static final double wz9084 = -0.16;
    // Дифференциальное различие масштабов
    private static final double ms9084 = -0.00000012;


    private static double latitude;
    private static double longitude;
    private static double altitude;


    public double getLatitude84() {
        return Math.toDegrees(latitude);
    }

    public double getLongitude84() {
        return Math.toDegrees(longitude);
    }

    public double getAltitude84() {
        return Math.rint((altitude) * 1000) / 1000;
    }

    public double getLatitude42() {
        return Math.toRadians(latitude);
    }

    public double getLongitude42() {
        return Math.toRadians(longitude);
    }

    public double getAltitude42() {
        return Math.rint((altitude) * 1000) / 1000;
    }

    public void Ck42ToWgs84Converter( double Bd, double Ld, double H ) {
        latitude = Bd + dB(Bd, Ld, H) / 3600;
        longitude = Ld + dL(Bd, Ld, H) / 3600;
        altitude = H + dH(Bd, Ld, H);
    }

    public void Wgs84ToCk42Converter( double Bd, double Ld, double H ) {

        latitude = Bd - dB(Bd, Ld, H) / 3600;
        longitude = Ld - dL(Bd, Ld, H) / 3600;
        altitude = H - dH(Bd, Ld, H);
    }

    private Double dB( Double Bd, Double Ld, Double H ) {
        Double B, L, M, N;


        B = Bd * Pi / 180;
        L = Ld * Pi / 180;
        M = a * (1 - e2) / Math.pow((1 - e2 * Math.pow(Math.sin(B), 2)), 1.5);
        N = a * Math.pow((1 - e2 * Math.pow(Math.sin(B), 2)), -0.5);
        return ro / (M + H) * (N / a * e2 * Math.sin(B) * Math.cos(B) * da + ((N * N) / (a * a) + 1) * N * Math.sin(B) * Math.cos(B) * de2 / 2 -
                (dx * Math.cos(L) + dy * Math.sin(L)) * Math.sin(B) + dz * Math.cos(B)) -
                wx * Math.sin(L) * (1 + e2 * Math.cos(2 * B)) +
                wy * Math.cos(L) * (1 + e2 * Math.cos(2 * B)) -
                ro * ms * e2 * Math.sin(B) * Math.cos(B);
    }

    private Double dL( Double Bd, Double Ld, Double H ) {
        Double B, L, N;

        B = Bd * Pi / 180;
        L = Ld * Pi / 180;
        N = a * Math.sqrt((1 - e2 * Math.pow(Math.sin(B), 2)));
        return ro / ((N + H) * Math.cos(B)) * (-dx * Math.sin(L) + dy * Math.cos(L)) +
                Math.tan(B) * (1 - e2) * (wx * Math.cos(L) + wy * Math.sin(L)) - wz;
    }

    private Double dH( Double Bd, Double Ld, Double H ) {
        Double B, L, N;

        B = Bd * Pi / 180;
        L = Ld * Pi / 180;
        N = a * Math.sqrt((1 - e2 * Math.pow(Math.sin(B), 2)));
        return -a / N * da + N * Math.pow(Math.sin(B), 2) * de2 / 2 + (dx * Math.cos(L) +
                dy * Math.sin(L)) * Math.cos(B) +
                dz * Math.sin(B) - N * e2 * Math.sin(B) * Math.cos(B) * (wx / ro * Math.sin(L) -
                wy / ro * Math.cos(L)) + (a * a / N + H) * ms;
    }
}
