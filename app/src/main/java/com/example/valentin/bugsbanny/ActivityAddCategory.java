package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.Toast;

public class ActivityAddCategory extends Activity implements
        ColorPickerDialog.OnColorChangedListener,
        AdapterView.OnItemLongClickListener {
    ListView listView;
    TextView textView_color;
    //    DBHelper dbHelper;
    private SimpleCursorAdapter madapter = null;
    Button btnOk, btnCancel, btnColor;
    EditText editText;
    protected int _color = Color.BLACK;
    int color_default;
    Paint mPaint;
    private final static int ID_EDIT_SUMM = 0;
    private final static int ID_DELETE_SUMM = 1;

    private long select_ID = -1;
    private String name_category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        listView = (ListView) findViewById(R.id.listView_category);
        listView.setAdapter(loadCategoryFromDB());
        listView.setOnItemLongClickListener(this);

        editText = (EditText) findViewById(R.id.edit_add_category);
        textView_color = (TextView) findViewById(R.id.textView_color);
        color_default = textView_color.getTextColors().getDefaultColor();
        btnOk = (Button) findViewById(R.id.btn_add_category);
        btnCancel = (Button) findViewById(R.id.btn_cancel_category);
        btnColor = (Button) findViewById(R.id.button_color);

        btnOk.setOnClickListener(onClickStart);
        btnCancel.setOnClickListener(onClickStart);
        btnColor.setOnClickListener(onClickStart);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }

    private SimpleCursorAdapter loadCategoryFromDB() {
        final Cursor c = Constant.dbHelper.getAllData();
        startManagingCursor(c);

        if (madapter != null) madapter.notifyDataSetChanged();

        if (c != null) {
            if (c.moveToFirst()) {
                madapter = new SimpleCursorAdapter(this,
                        R.layout.list_view_add_category, c,
                        new String[]{"_id", Constant.dbHelper.COLUMN_TXT},
                        new int[]{R.id.iconCategory, R.id.textCategory});

                madapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (view.getId() == R.id.iconCategory) {
                            int num = c.getColumnIndex("_id");
                            int id = c.getInt(num);
                            ((ImageView) view).setImageResource(Constant.get_Picture_Index_Default(id));
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        madapter.notifyDataSetChanged();
        return madapter;
    }

    View.OnClickListener onClickStart = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_category:
                    if (!editText.getText().toString().trim().equals("")) {
                        if (_color == Color.BLACK) {
                            Toast.makeText(ActivityAddCategory.this,
                                    R.string.choose_color_title_err, Toast.LENGTH_LONG).show();
                        } else {
                            Constant.dbHelper.addRec(editText.getText().toString(), _color);
                            listView.setAdapter(loadCategoryFromDB());                                //notifyDataSetChanged();
                            editText.setText(null);
                            _color = Color.BLACK;
                            textView_color.setTextColor(color_default);
                        }
                    }
                    break;
                case R.id.btn_cancel_category:
                    finish();
                    break;
                case R.id.button_color:
                    mPaint.setColor(Color.BLUE);
                    new ColorPickerDialog(ActivityAddCategory.this, ActivityAddCategory.this, mPaint.getColor()).show();
                    break;
            }
        }
    };

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
        _color = color;
        textView_color.setTextColor(color);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 7) {
            registerForContextMenu(parent);
            openContextMenu(parent);
            select_ID = id;
            TextView text_name_category = (TextView) view.findViewById(R.id.textCategory);
            if (text_name_category != null) name_category = text_name_category.getText().toString();
        } else {
            Toast.makeText(ActivityAddCategory.this, R.string.msg_edit_del_category, Toast.LENGTH_LONG).show();
        }
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
                View layout = inflater.inflate(R.layout.dialog_edit_category, (ViewGroup) findViewById(R.id.layout_category));
                Button btnSave = (Button) layout.findViewById(R.id.button_save_category);
                Button btnCancel = (Button) layout.findViewById(R.id.button_cancel_edit_category);
                final EditText editText = (EditText) layout.findViewById(R.id.save_edit_category_dialog);
                editText.setText(name_category);

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getText(R.string.menu_edit_summ));
                builder.setView(layout);
                builder.setCancelable(false);
                final AlertDialog dlg = builder.create();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!editText.getText().toString().equals(""))
                            if (Constant.dbHelper.update_Category_Name(select_ID, editText.getText().toString()) > 0) {
                                madapter.getCursor().requery();
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
                        "(" + name_category + ")  ?");
                builder_del.setCancelable(false);
                builder_del.setPositiveButton(getText(R.string.menu_delete_summ),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                if (Constant.dbHelper.delete_Category(select_ID) > 0) {
                                    madapter.getCursor().requery();
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
}

