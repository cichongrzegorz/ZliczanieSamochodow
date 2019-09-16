package com.cichon.plik;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ZapisPliku {

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");

    private String nazwaPliku;


    public ZapisPliku() {
        this.incjalizuj();
    }

    public void incjalizuj() {
        this.nazwaPliku = "raport-" + FORMATTER.format(new Date()) + ".txt";
    }

    public void zapisz(String data) {
        try {
            File filesDir = Environment.getExternalStorageDirectory();
            File filesDir2 = new File(filesDir, "/DCIM");

            File f = new File(filesDir2, nazwaPliku);
            FileWriter out = new FileWriter(f);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
