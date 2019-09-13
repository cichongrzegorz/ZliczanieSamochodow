package com.cichon.plik;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ZapisPliku {
    private String nazwaPliku;

    public ZapisPliku(String nazwaPliku) {
        this.nazwaPliku = nazwaPliku;
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

    public String getNazwaPliku() {
        return nazwaPliku;
    }
}
