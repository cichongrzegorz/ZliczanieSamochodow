package com.cichon;

public class TrybAplikacji {

    public Tryb getTryb() {
        return tryb;
    }

    enum Tryb {
        LICZENIE,
        OBRAZ_REFERENCYJNY,
        PUNKTY
    }

    private Tryb tryb = Tryb.LICZENIE;

    public void ustawLiczenie() {
        this.tryb = Tryb.LICZENIE;
    }

    public void ustawObrazReferencyjny() {
        this.tryb = Tryb.OBRAZ_REFERENCYJNY;
    }

    public void ustawPunkty() {
        this.tryb = Tryb.PUNKTY;
    }
}
