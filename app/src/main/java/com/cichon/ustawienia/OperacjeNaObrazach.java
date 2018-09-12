package com.cichon.ustawienia;

public class OperacjeNaObrazach implements Ustawienia {

    public int szerokosc;
    public int wysokosc;
    public double sigma;

    public int progowanie;

    private ObserwatorUstawien obserwator;


    @Override
    public void resetuj() {
        this.szerokosc = 21;
        this.wysokosc = 21;
        this.sigma = 0;

        this.progowanie = 70;

        this.obserwator.zmienionoUstawienia();
    }

    @Override
    public void dodajObserwatora(ObserwatorUstawien obserwator) {
        this.obserwator = obserwator;
    }
}
