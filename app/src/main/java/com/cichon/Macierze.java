package com.cichon;

import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Map;

public class Macierze {
    Map<String, Mat> macierze;

    public Macierze() {
        macierze = new HashMap<>();
    }

    public Mat dodaj(String klucz, Mat macierz) {
        this.macierze.put(klucz, macierz);
        return this.pobierz(klucz);
    }

    public Mat dodajPusta(String klucz) {
        return dodaj(klucz, new Mat());
    }

    public Mat pobierz(String klucz) {
        return this.macierze.get(klucz);
    }

    public Mat pobierzLubDodajIPobierz(String klucz) {
        Mat macierz = this.macierze.get(klucz);
        if (macierz == null) {
            return this.dodajPusta(klucz);
        }
        return macierz;
    }

    public void zwolnijWszystkie() {
        for (Mat macierz : macierze.values()) {
            if (macierz != null)
                macierz.release();
        }
    }
}
