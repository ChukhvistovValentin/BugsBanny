package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;

public class ActivityPaintGraphView extends Activity {
    Button btn;
    Spinner lvMonth, lvYear;
    SeekBar seekBar;
    TextView textSeekPos;
    ArrayList<String> year_arr = new ArrayList<String>();
    String[] orderGraph = {"summ desc", "summ", "tc._id"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_graph_view);

        btn = (Button) findViewById(R.id.btn_graph);
        btn.setOnClickListener(onClickGraph);

        lvMonth = (Spinner) findViewById(R.id.spinner_month);
        lvYear = (Spinner) findViewById(R.id.spinner_year);

//        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
//                this, R.array.names_month, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] m = getResources().getStringArray(R.array.names_month);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, m);
        adapter.setDropDownViewResource(R.layout.row_spinner_month_diagram);
        lvMonth.setAdapter(adapter);
        lvMonth.setSelection(Constant.getCurrentMonth());

        updateYearArr(0);
//        final ArrayAdapter<String> adapter_y = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, year_arr);
//        adapter_y.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<String> adapter_y = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, year_arr);
        adapter_y.setDropDownViewResource(R.layout.row_spinner_month_diagram);
        lvYear.setAdapter(adapter_y);
        lvYear.setSelection(3);
        lvYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = lvYear.getSelectedItem().toString();
                adapter_y.notifyDataSetChanged();
                updateYearArr(Integer.valueOf(s));
                lvYear.setSelection(3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        textSeekPos = (TextView) findViewById(R.id.textSeekPos);
        textSeekPos.setText(getString(R.string.s2));

        seekBar = (SeekBar) findViewById(R.id.seekBarOrder);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

    }

    View.OnClickListener onClickGraph = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_graph:
                    int year, month, day;
                    year = Integer.valueOf(lvYear.getSelectedItem().toString());
                    month = lvMonth.getSelectedItemPosition();
                    day = 1;
                    Date date = new Date(year, month, day);
                    RadioGroup rg = (RadioGroup) findViewById(R.id.radiogrButtonDiagram);
                    switch (rg.getCheckedRadioButtonId()) {
                        case R.id.radioButtonDiagram1:
                            Intent intent = new Intent(ActivityPaintGraphView.this, ActivityViewGraph.class);
                            intent.putExtra("DATE_B", setDateToStr(date, true));
                            intent.putExtra("DATE_E", setDateToStr(date, false));
                            intent.putExtra("ORDER", orderGraph[seekBar.getProgress()]);
                            startActivity(intent);
                            break;
                        case R.id.radioButtonDiagram2:
                            Intent intent2 = new Intent(ActivityPaintGraphView.this, ActivityViewGraph2.class);
                            intent2.putExtra("DATE_B", setDateToStr(date, true));
                            intent2.putExtra("DATE_E", setDateToStr(date, false));
                            intent2.putExtra("ORDER", orderGraph[seekBar.getProgress()]);
                            startActivity(intent2);
                            break;
                        default:
                            break;
                    }
            }
        }
    };

    private String setDateToStr(Date date, boolean B_E) {
        String day_, month_, time_;
        int year, month, day;
        if (B_E) {
            time_ = " 00:00:00";
            day_ = "01";
            month = date.getMonth() + 1;
        } else {
            time_ = " 23:59:59";
            year = date.getYear();
            month = date.getMonth() + 1;

            day = new Date(year, month, 0).getDate();
            if (day <= 9) {
                day_ = "0" + day;
            } else {
                day_ = "" + day;
            }
        }
        if (month <= 9) {
            month_ = "0" + month;
        } else {
            month_ = "" + month;
        }
        StringBuilder s = new StringBuilder().
                append(date.getYear()).append("-").
                append(month_).append("-").append(day_).append(time_);
//                append(" ");
        String ss = s.toString();
        return ss;
    }

    private void updateYearArr(int year) {
        int currentYear;
        if (year == 0) {
            java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getDefault(),
                    java.util.Locale.getDefault());
            calendar.setTime(new java.util.Date());
            currentYear = calendar.get(java.util.Calendar.YEAR);
        } else {
            year_arr.clear();
            currentYear = year;
        }
        for (int i = 3; i > 0; i--) {
            year_arr.add(3 - i, "" + (currentYear - i));
        }
        year_arr.add(3, "" + currentYear);
        for (int i = 5; i < 8; i++) {
            year_arr.add(i - 1, "" + (currentYear + i - 4));
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String seekTextPos[] = {getString(R.string.s2), getString(R.string.s3), getString(R.string.s1)};
                    textSeekPos.setText(seekTextPos[seekBar.getProgress()]);
                }
            };

}
