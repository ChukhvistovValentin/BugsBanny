package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityViewExpensesDetail extends Activity implements AdapterView.OnItemLongClickListener {
    long ID;
    Cursor c = null;
    ListView listView;
    String date_B, date_E;

    private final static int ID_EDIT_SUMM = 0;
    private final static int ID_DELETE_SUMM = 1;

    private long select_ID = -1;
    private String summ_category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ID = intent.getLongExtra("ID", -1);
        date_B = intent.getStringExtra("DATE_B");
        date_E = intent.getStringExtra("DATE_E");

        if ((ID > -1) && (!date_B.equals("")) && (!date_E.equals(""))) {
            setContentView(R.layout.activity_view_expenses_detail);
            listView = (ListView) findViewById(R.id.listView_exp_detail);
            listView.setOnItemLongClickListener(this);
            LoadAllDataDetail(ID);
        } else {
            setContentView(R.layout.fragment_detail_empty);
        }
    }

    private void LoadAllDataDetail(long id) {
        c = Constant.dbHelper.getAllDataDetail(id, date_B, date_E);
        startManagingCursor(c);
        if (c != null) {
            if (c.moveToFirst()) {
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                        R.layout.row_exp_detail, c,
                        new String[]{Constant.dbHelper.DB_TABLE_T_ID,
                                Constant.dbHelper.DB_TABLE_DATE, Constant.dbHelper.COLUMN_SUMM},
                        new int[]{R.id.imageView_exp_detail, R.id.text_date_exp_detail, R.id.text_name_exp_detail});

                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (view.getId() == R.id.imageView_exp_detail) {
                            int num = c.getColumnIndex(Constant.dbHelper.DB_TABLE_T_ID);
                            int id = c.getInt(num);
                            ((ImageView) view).setImageResource(Constant.get_Picture_Index_Default(id));
                            return true;
                        }else if (view.getId() == R.id.text_date_exp_detail) {
                            int num = c.getColumnIndex(Constant.dbHelper.DB_TABLE_DATE);
                            String id = c.getString(num);
                            try {
//                                ((TextView) view).setText(id);
                                Date date;
                                DateFormat formatter;
                                formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
                                date = formatter.parse(id);

                                String mask = new SimpleDateFormat("dd MMMM yyyy \nhh:mm:ss").format(date);
                                ((TextView) view).setText(mask);
                            } catch (Exception e) {
                                ((TextView) view).setText(id);
                            }
                            return true;
                        }
                        return false;
                    }
                });
                listView.setAdapter(adapter);
            } else {
                setContentView(R.layout.fragment_detail_empty);
            }
        } else {
            setContentView(R.layout.fragment_detail_empty);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        registerForContextMenu(parent);
        openContextMenu(parent);
        select_ID = id;
        TextView text_summ_category = (TextView) view.findViewById(R.id.text_name_exp_detail);
        if (text_summ_category != null) summ_category = text_summ_category.getText().toString();
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.menu_title));

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_edit_summ, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete_summ:
                createAlertDialog(ID_DELETE_SUMM);
                return true;
            case R.id.item_edit_summ:
                createAlertDialog(ID_EDIT_SUMM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void createAlertDialog(int id) {
        switch (id) {
            case ID_EDIT_SUMM:
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_edit_summ, (ViewGroup) findViewById(R.id.layout_summ));
                Button btnSave = (Button) layout.findViewById(R.id.button_save_summ);
                Button btnCancel = (Button) layout.findViewById(R.id.button_cancel_edit);
                final EditText editText = (EditText) layout.findViewById(R.id.save_edit_sum_dialog);
                editText.setText(summ_category);

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getText(R.string.menu_edit_summ));
                builder.setView(layout);
                builder.setCancelable(false);
                final AlertDialog dlg = builder.create();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ((!editText.getText().toString().equals("")) && (Float.parseFloat(editText.getText().toString()) > 0))
                            if (Constant.dbHelper.update_Summ(select_ID, Float.parseFloat(editText.getText().toString())) > 0) {
                                LoadAllDataDetail(ID);
                                dlg.dismiss();
                            }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dlg.dismiss();
                    }
                });
                dlg.show();
                break;

            case ID_DELETE_SUMM:
                final AlertDialog.Builder builder_del = new AlertDialog.Builder(this);
                builder_del.setTitle(getText(R.string.menu_delete_summ));
                builder_del.setMessage(getText(R.string.menu_delete_summ_message) +
                        "(" + summ_category + getText(R.string.grn) + ")  ?");
                builder_del.setCancelable(false);

                builder_del.setPositiveButton(getText(R.string.menu_delete_summ),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                if (Constant.dbHelper.delete_Summ(select_ID) > 0) {
                                    LoadAllDataDetail(ID);
                                }
                            }
                        });
                builder_del.setNegativeButton(getText(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                            }
                        });

                builder_del.create();
                builder_del.show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null) c.close();
    }
}
