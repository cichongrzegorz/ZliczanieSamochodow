package com.cichon.ustawienia;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cichon.R;

public class OperacjeNaObrazach implements Ustawienia {

    private UstawieniaAplikacji ustawieniaAplikacji;

    private SeekBar szerokoscGaussSeek;
    private SeekBar wysokoscGaussSeek;
    private SeekBar sigmaGaussSeek;

    private TextView szerokoscText;
    private TextView wysokoscText;
    private TextView sigmaText;

    public OperacjeNaObrazach(UstawieniaAplikacji ustawienia) {
        this.ustawieniaAplikacji = ustawienia;
    }

    @Override
    public void inicjalizuj(View view) {

        wysokoscText = view.findViewById(R.id.wysokoscText);
        szerokoscText = view.findViewById(R.id.szerokoscText);
        sigmaText = view.findViewById(R.id.sigmaText);


        wysokoscGaussSeek = view.findViewById(R.id.wysokoscGaussSeek);
        wysokoscGaussSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int wysokosc = progress * 2 + 1;
                ustawieniaAplikacji.setWysokosc(wysokosc);
                wysokoscText.setText("Wysokość: " + wysokosc);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        szerokoscGaussSeek = view.findViewById(R.id.szerokoscGaussSeek);
        szerokoscGaussSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int szerokosc = progress * 2 + 1;
                ustawieniaAplikacji.setSzerokosc(szerokosc);
                szerokoscText.setText("Szerokość: " + szerokosc);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sigmaGaussSeek = view.findViewById(R.id.sigmaGaussSeek);
        sigmaGaussSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ustawieniaAplikacji.setSigma(progress);
                sigmaText.setText("Sigma: " + progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void zresetowanoUstawienia() {
        this.szerokoscGaussSeek.setProgress(ustawieniaAplikacji.getSzerokosc() / 2);
        this.wysokoscGaussSeek.setProgress(ustawieniaAplikacji.getWysokosc() / 2);
        this.sigmaGaussSeek.setProgress((int) ustawieniaAplikacji.getSigma());

        wysokoscText.setText("Wysokość: " + (ustawieniaAplikacji.getWysokosc()));
        szerokoscText.setText("Szerokość: " + (ustawieniaAplikacji.getSzerokosc()));
        sigmaText.setText("Sigma: " + ((int) ustawieniaAplikacji.getSigma()));
    }
}
