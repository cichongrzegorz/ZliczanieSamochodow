package com.cichon.detection;

public class Samochody {
    public int maxTir;
    public int minOso;

    public Samochody() {
        this.resetuj();
    }

    public void aktualizuj(int maxTir, int minOso) {
        if (maxTir > this.maxTir) {
            this.maxTir = maxTir;
        }
        if (minOso < this.minOso) {
            this.minOso = minOso;
        }
    }

    public void resetuj() {
        maxTir = 0;
        minOso = Integer.MAX_VALUE;
    }
}
