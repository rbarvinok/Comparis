package ua.euler.javaclass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static ua.euler.controller.Controller.pressureNull;

public class GetSettings {
    public void getPressureNull() throws IOException {

        FileReader fileReader1 = new FileReader("settings.txt");
        BufferedReader bufferedReader1 = new BufferedReader(fileReader1);

        int lineNumber1 = 0;
        String line1 = "";
        while ((line1 = bufferedReader1.readLine()) != null) {

            if (lineNumber1 == 0) {
                pressureNull = Double.parseDouble(line1.split("=")[1]); 
            }
        }
        fileReader1.close();
    }

}
