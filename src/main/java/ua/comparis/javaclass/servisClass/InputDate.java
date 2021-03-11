package ua.comparis.javaclass.servisClass;

import java.util.ArrayList;
import java.util.Arrays;

public class InputDate {
    private ArrayList<String> items = new ArrayList<>();

    public InputDate(String[] items){
        this.items.addAll(Arrays.asList(items));
    }

    public ArrayList<String> getItems(){
        return items;
    }
}
