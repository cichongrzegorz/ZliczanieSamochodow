package com.cichon.ustawienia;

public class UstawieniaAplikacji {

    private String obraz;

    public int szerokosc;
    public int wysokosc;
    public double sigma;

    public int progowanie;

    public int wielkoscSamochodu;
    public double przelicznikCiezarowki;

    public UstawieniaAplikacji() {
        this.resetuj();
    }

    public void resetuj() {
        this.obraz = "Zliczanie";

        this.szerokosc = 21;
        this.wysokosc = 21;
        this.sigma = 0;

        this.progowanie = 70;

        this.wielkoscSamochodu = 4000;
        this.przelicznikCiezarowki = 2.0;
    }

    public int getSzerokosc() {
        return szerokosc;
    }

    public void setSzerokosc(int szerokosc) {
        this.szerokosc = szerokosc;
    }

    public int getWysokosc() {
        return wysokosc;
    }

    public void setWysokosc(int wysokosc) {
        this.wysokosc = wysokosc;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public int getProgowanie() {
        return progowanie;
    }

    public void setProgowanie(int progowanie) {
        this.progowanie = progowanie;
    }

    public int getWielkoscSamochodu() {
        return wielkoscSamochodu;
    }

    public void setWielkoscSamochodu(int wielkoscSamochodu) {
        this.wielkoscSamochodu = wielkoscSamochodu;
    }

    public double getPrzelicznikCiezarowki() {
        return przelicznikCiezarowki;
    }

    public void setPrzelicznikCiezarowki(double przelicznikCiezarowki) {
        this.przelicznikCiezarowki = przelicznikCiezarowki;
    }

    public String getObraz() {
        return obraz;
    }

    public void setObraz(String obraz) {
        this.obraz = obraz;
    }
}
