package com.cichon.ustawienia;

public class Pojazdy implements Ustawienia {

    public int wielkoscSamochodu;
    public double przelicznikCiezarowki;
    private ObserwatorUstawien obserwator;

    @Override
    public void resetuj() {
        this.wielkoscSamochodu = 300;
        this.przelicznikCiezarowki = 2.0;
        this.obserwator.zmienionoUstawienia();
    }

    @Override
    public void dodajObserwatora(ObserwatorUstawien obserwator) {
        this.obserwator = obserwator;
    }
}
