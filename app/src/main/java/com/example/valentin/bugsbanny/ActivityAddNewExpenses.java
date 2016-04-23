package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityAddNewExpenses extends Activity implements
        CompoundButton.OnCheckedChangeListener {
    TextView tv_category, tv_date;
    Button btnAdd;
    Switch aSwitch;
    DatePicker datePicker;
    EditText editText;
    Integer id_t = -1;
    String button_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_expenses);

        button_text = getString(R.string.add_exp);
        Intent intent = getIntent();
        String c_name = intent.getStringExtra("CATEGORY");
        id_t = intent.getIntExtra("ID_T", -1);

        tv_category = (TextView) findViewById(R.id.category_name_d);
        tv_date = (TextView) findViewById(R.id.text_date_now);

        tv_category.setText(c_name);
        tv_date.setText(getDate());

        editText = (EditText) findViewById(R.id.edit_summ);

        btnAdd = (Button) findViewById(R.id.button_add_exp);
        btnAdd.setOnClickListener(onClickStart);
        btnAdd.setText(button_text + "\n" + tv_date.getText());

        Calendar today = Calendar.getInstance();

        datePicker = (DatePicker) findViewById(R.id.datePicker_switch);
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String tmp = String.format("%02d.%02d", dayOfMonth, (monthOfYear + 1));
                        btnAdd.setText(button_text + "\n" + tmp + "." + year);
                    }
                });


        aSwitch = (Switch) findViewById(R.id.switch_date);
        aSwitch.setOnCheckedChangeListener(this);
        loadPicture(id_t);
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");//("dd.MM.yyyy hh:mm");
        return dateFormat.format(new Date());
    }

    private String getDate(boolean system) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        if (!system) return dateFormat.format(new Date());
        else {
            int month = datePicker.getMonth() + 1;
            String month_;
            if (month <= 9)
                month_ = "0" + month;
            else
                month_ = "" + month;

            int day = datePicker.getDayOfMonth();
            String day_;
            if (day <= 9)
                day_ = "0" + day;
            else
                day_ = "" + day;

            long h = new Date().getHours();
            long m = new Date().getMinutes();
            long s = new Date().getSeconds();

            String time_ = " " + h + ":" + m + ":" + s;
            StringBuilder sb = new StringBuilder().
                    append(datePicker.getYear()).append("-").
                    append(month_).append("-").append(day_).
                    append(time_);
            return sb.toString();
        }
    }

    View.OnClickListener onClickStart = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String date_add = getDate(aSwitch.isChecked());
            switch (v.getId()) {
                case R.id.button_add_exp:
                    if (!editText.getText().toString().trim().equals("") && (id_t > -1)) {
                        if (Float.parseFloat(editText.getText().toString()) > 0) {
                            if (Constant.dbHelper.addRecExp(id_t,
                                    Float.parseFloat(editText.getText().toString()), date_add) > 0) {
                                Toast.makeText(ActivityAddNewExpenses.this, getString(R.string.success_add_exp),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(ActivityAddNewExpenses.this, getString(R.string.err_add_exp),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    break;
            }
        }
    };

    private void loadPicture(int id) {
        ImageView image = (ImageView) findViewById(R.id.imageView_category);
        if ((id > -1) && (id < 9)) {
            image.setImageResource(Constant.get_Picture_Index_Default(id));
        } else if ((id > 8) || (id < 0)) {
            image.setImageResource(R.drawable.other);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            datePicker.setVisibility(View.VISIBLE);
            String tmp = String.format("%02d.%02d.%d", datePicker.getDayOfMonth(),
                    (datePicker.getMonth() + 1), datePicker.getYear());
            btnAdd.setText(button_text + "\n" + tmp);
        } else {
            datePicker.setVisibility(View.INVISIBLE);
            btnAdd.setText(button_text + "\n" + tv_date.getText());
        }
    }
}
