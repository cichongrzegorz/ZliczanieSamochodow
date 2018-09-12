package com.cichon.ustawienia;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.cichon.R;

public class Pojazdy implements Ustawienia {

    private UstawieniaAplikacji ustawieniaAplikacji;

    private EditText wielkoscSamochodu;
    private EditText przelicznikCiezarowki;

    public Pojazdy(UstawieniaAplikacji ustawieniaAplikacji) {
        this.ustawieniaAplikacji = ustawieniaAplikacji;
    }

    @Override
    public void inicjalizuj(View view) {

        wielkoscSamochodu = view.findViewById(R.id.wielkoscSamochodu);
        wielkoscSamochodu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ustawieniaAplikacji.setWielkoscSamochodu(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        przelicznikCiezarowki = view.findViewById(R.id.przelicznikCiezarowki);
        przelicznikCiezarowki.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ustawieniaAplikacji.setPrzelicznikCiezarowki(Double.parseDouble(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void zresetowanoUstawienia() {
        this.przelicznikCiezarowki.setText(ustawieniaAplikacji.getPrzelicznikCiezarowki() + "");
        this.wielkoscSamochodu.setText(ustawieniaAplikacji.getWielkoscSamochodu() + "");
    }
}
