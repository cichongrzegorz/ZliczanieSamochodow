package com.cichon;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Settings {

    String wybranaOpcja;
    int lowTreshold;
    int highTreshold;
    int rozmiar;
    boolean isSkalaSzarosci = false;

    private SeekBar sb1;
    private SeekBar sb2;

    private TextView tv1;
    private TextView tv2;

    private Switch skalaSzarosci;

    private EditText rozmiarView;

    private Spinner opcja;

    public Settings() {
        reset();
    }

    public void reset() {
        lowTreshold = 80;
        highTreshold = 200;
        rozmiar = 300;
        isSkalaSzarosci = false;
    }

    public void initialize(final View layout) {

        sb1 = layout.findViewById(R.id.seekBar);
        sb1.setProgress(lowTreshold);
        sb1.setMax(200);
        sb2 = layout.findViewById(R.id.seekBar2);
        sb2.setMax(400);
        sb2.setProgress(highTreshold);

        tv1 = layout.findViewById(R.id.textView);
        tv2 = layout.findViewById(R.id.textView2);

        tv1.setText("low treshold: " + sb1.getProgress() + "/" + sb1.getMax());
        tv2.setText("high treshold: " + sb2.getProgress() + "/" + sb2.getMax());

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                lowTreshold = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv1.setText("low treshold: " + lowTreshold + "/" + seekBar.getMax());
            }
        });

        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                highTreshold = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv2.setText("high treshold: " + highTreshold + "/" + seekBar.getMax());
            }
        });

        skalaSzarosci = layout.findViewById(R.id.skalaSzarosci);
        skalaSzarosci.setChecked(isSkalaSzarosci);
        skalaSzarosci.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSkalaSzarosci = isChecked;
            }
        });

        rozmiarView = layout.findViewById(R.id.rozmiarObiektu);
        rozmiarView.setText(rozmiar + "");
        rozmiarView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    rozmiar = Integer.parseInt(s.toString());
                } catch (NumberFormatException ex) {
                    Toast.makeText(layout.getContext(), "Proszę podać poprawną liczbę", Toast.LENGTH_SHORT).show();
                    rozmiarView.setText(rozmiar + "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        opcja = layout.findViewById(R.id.process_step);
        opcja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wybranaOpcja = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
