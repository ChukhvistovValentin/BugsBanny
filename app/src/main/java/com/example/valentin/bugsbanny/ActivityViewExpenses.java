package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityViewExpenses extends Activity implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {
    Cursor c = null;
    SimpleCursorAdapter adapter;
    Spinner spinner;
    Button datePicker, datePicker_end;
    Button button_view_detail;
    Switch aSwitch;
    long id_ = -1;
    Boolean swith_position = false;
    Calendar dateandtime, dateandtime_b, dateandtime_e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        dateandtime = Calendar.getInstance(Locale.UK);
        dateandtime_b = Calendar.getInstance(Locale.UK);
        dateandtime_e = Calendar.getInstance(Locale.UK);

        datePicker = (Button) findViewById(R.id.datePicker_category);
        datePicker_end = (Button) findViewById(R.id.datePicker_category_end);
        final Calendar c = Calendar.getInstance();
        //datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
        //datePicker_end.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
        datePicker.setText(new SimpleDateFormat("dd MMMM yyyy").format(c.getTime()));
        datePicker_end.setText(new SimpleDateFormat("dd MMMM yyyy").format(c.getTime()));
        datePicker.setOnClickListener(onClickViewData);
        datePicker_end.setOnClickListener(onClickViewData);

        spinner = (Spinner) findViewById(R.id.spinner_category);

        loadCategoryFromDB(spinner);
        spinner.setOnItemSelectedListener(this);

        button_view_detail = (Button) findViewById(R.id.button_view_detail);
        button_view_detail.setOnClickListener(onClickViewDetail);

        aSwitch = (Switch) findViewById(R.id.switch_view_all_category);
        if (aSwitch != null) {
            aSwitch.setOnCheckedChangeListener(this);
        }
    }

    private void loadCategoryFromDB(Spinner spinner_c) {
        c = Constant.dbHelper.getAllData();
        startManagingCursor(c);
        if (c != null) {
            if (c.moveToFirst()) {
                adapter = new SimpleCursorAdapter(this,
                        R.layout.row_spinner, c,
                        new String[]{"_id", Constant.dbHelper.COLUMN_TXT},
                        new int[]{R.id.icon_spinner, R.id.spiner_text});
                adapter.setDropDownViewResource(R.layout.row_spinner_view_exp);

                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (view.getId() == R.id.icon_spinner) {
                            int num = c.getColumnIndex("_id");
                            int id = c.getInt(num);
                            ((ImageView) view).setImageResource(Constant.get_Picture_Index_Default(id));
                            return true;
                        }
                        return false;
                    }
                });

                spinner_c.setAdapter(adapter);
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        id_ = id;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        id_ = -1;
    }

    private String getDatePicker_(boolean B_E) {
//        DatePicker pickerDate;
        Calendar pickerDate;
        String day_, month_, time_;
        if (B_E) {
            pickerDate = dateandtime_b;//(DatePicker) findViewById(R.id.datePicker_category);
            time_ = " 00:00:00";
        } else {
            pickerDate = dateandtime_e;//(DatePicker) findViewById(R.id.datePicker_category_end);
            time_ = " 23:59:59";
        }
        int day = pickerDate.get(Calendar.DAY_OF_MONTH);//getDayOfMonth();
        if (day <= 9) {
            day_ = "0" + day;
        } else {
            day_ = "" + day;
        }
        int month = pickerDate.get(Calendar.MONTH) + 1;//getMonth() + 1;
        if (month <= 9) {
            month_ = "0" + month;
        } else {
            month_ = "" + month;
        }
        StringBuilder s = new StringBuilder().
                append(pickerDate.get(Calendar.YEAR))//getYear())
                .append("-").
                        append(month_).append("-").append(day_).
                        append(time_);// append(" ");
        String ss = s.toString();
        return ss;
    }

    private String getDateBegin() {
        String day_, month_;
//        DatePicker pickerDate = (DatePicker) findViewById(R.id.datePicker_category_end);
        Calendar pickerDate = dateandtime_e;
        int day = pickerDate.get(Calendar.DAY_OF_MONTH);//getDayOfMonth();
        if (day <= 9) {
            day_ = "0" + day;
        } else {
            day_ = "" + day;
        }
        int month = pickerDate.get(Calendar.MONTH) + 1;//getMonth() + 1;
        if (month <= 9) {
            month_ = "0" + month;
        } else {
            month_ = "" + month;
        }
        StringBuilder s = new StringBuilder().
                append(pickerDate.get(Calendar.YEAR))//getYear())
                .append("-").
                        append(month_).append("-").append(day_).
                        append(" 00:00:00");// append(" ");
        String ss = s.toString();
        return ss;
    }

    View.OnClickListener onClickViewDetail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.button_view_detail:
                    String b = getDatePicker_(true);
                    String e = getDatePicker_(false);
                    if (!swith_position) {
                        intent = new Intent(ActivityViewExpenses.this, ActivityViewExpensesDetail.class);
                        intent.putExtra("ID", id_);
                    } else {
                        intent = new Intent(ActivityViewExpenses.this, ActivityViewExpensesDetailAll.class);
                        b = getDateBegin();
                    }
                    intent.putExtra("DATE_B", b);
                    intent.putExtra("DATE_E", e);
                    startActivity(intent);
            }
        }
    };

    View.OnClickListener onClickViewData = new View.OnClickListener() {
        @Override
        public void onClick(final View arg0) {
            switch (arg0.getId()) {
                case R.id.datePicker_category:
                case R.id.datePicker_category_end:
                    if (arg0.getId() == R.id.datePicker_category)
                        dateandtime = dateandtime_b;
                    else
                        dateandtime = dateandtime_e;

                    DatePickerDailog dp = new DatePickerDailog(ActivityViewExpenses.this,
                            dateandtime, new DatePickerDailog.DatePickerListner() {
                        @Override
                        public void OnDoneButton(Dialog datedialog, Calendar c) {
                            datedialog.dismiss();
                            dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
                            dateandtime.set(Calendar.MONTH, c.get(Calendar.MONTH));
                            dateandtime.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
                            ((Button) arg0).setText(new SimpleDateFormat("dd MMMM yyyy").format(c.getTime()));

                            if (arg0.getId() == R.id.datePicker_category)
                                dateandtime_b = c;
                            else
                                dateandtime_e = c;
                        }

                        @Override
                        public void OnCancelButton(Dialog datedialog) {
                            datedialog.dismiss();
                        }
                    });
                    dp.show();
                    break;
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int visibility = (isChecked == true) ? View.INVISIBLE : View.VISIBLE;
//        spinner.setEnabled(!isChecked);
//        datePicker.setEnabled(!isChecked);
        TextView t1 = (TextView) findViewById(R.id.textView5);
        TextView t2 = (TextView) findViewById(R.id.textView3);
        t1.setVisibility(visibility);
        t2.setVisibility(visibility);
        spinner.setVisibility(visibility);
        datePicker.setVisibility(visibility);
        swith_position = isChecked;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null) c.close();
    }
}
