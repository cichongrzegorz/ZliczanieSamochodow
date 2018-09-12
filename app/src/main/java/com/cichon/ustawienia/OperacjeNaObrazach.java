package com.cichon.ustawienia;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.cichon.R;

import java.util.Arrays;
import java.util.List;

public class OperacjeNaObrazach implements Ustawienia {

    private List<String> wartosci = Arrays.asList(new String[]{"Zliczanie", "Rozmycie Gaussa", "Progowanie Binarne", "Obraz Referencyjny"});

    private UstawieniaAplikacji ustawieniaAplikacji;

    private Spinner obraz;
    private Switch skalaSzarosci;

    private EditText szerokoscGauss;
    private EditText wysokoscGauss;
    private EditText sigmaGauss;
    private EditText progowanieBinarne;

    public OperacjeNaObrazach(UstawieniaAplikacji ustawienia) {
        this.ustawieniaAplikacji = ustawienia;
    }

    @Override
    public void inicjalizuj(View view) {
        skalaSzarosci = view.findViewById(R.id.skalaSzarosci);
        skalaSzarosci.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ustawieniaAplikacji.setSkalaSzarosci(isChecked);
            }
        });

        szerokoscGauss = view.findViewById(R.id.szerokoscGauss);
        szerokoscGauss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ustawieniaAplikacji.setSzerokosc(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        wysokoscGauss = view.findViewById(R.id.wysokoscGauss);
        wysokoscGauss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ustawieniaAplikacji.setWysokosc(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sigmaGauss = view.findViewById(R.id.sigmaGauss);
        sigmaGauss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ustawieniaAplikacji.setSigma(Double.parseDouble(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        progowanieBinarne = view.findViewById(R.id.progowanieBinarne);
        progowanieBinarne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ustawieniaAplikacji.setProgowanie(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        obraz = view.findViewById(R.id.pokazObraz);
        obraz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ustawieniaAplikacji.setObraz((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void zresetowanoUstawienia() {
        this.skalaSzarosci.setChecked(ustawieniaAplikacji.isSkalaSzarosci());
        this.szerokoscGauss.setText(ustawieniaAplikacji.getSzerokosc() + "");
        this.wysokoscGauss.setText(ustawieniaAplikacji.getWysokosc() + "");
        this.sigmaGauss.setText(ustawieniaAplikacji.getSigma() + "");
        this.progowanieBinarne.setText(ustawieniaAplikacji.getProgowanie() + "");
        this.obraz.setSelection(wartosci.indexOf(ustawieniaAplikacji.getObraz()));
    }
}
