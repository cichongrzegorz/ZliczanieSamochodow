package com.cichon;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class ObszarSprawdzania {

    private static final int LICZBA_PUNKTOW = 4;

    List<Point> punkty;

    public ObszarSprawdzania() {
        this.punkty = new ArrayList<>();
    }

    public void dodajNastepny(float x, float y) {
        if (this.obszarUstawiony()) {
            return;
        }
        this.punkty.add(new Point(x, y));
    }

    public boolean obszarUstawiony() {
        return this.punkty.size() == LICZBA_PUNKTOW;
    }

    public void resetuj() {
        this.punkty.clear();
    }

    public Point[] pobierzTablicePunktow() {
        return punkty.toArray(new Point[0]);
    }
}
