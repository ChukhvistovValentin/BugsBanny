package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ActivityViewExpensesDetailAll extends Activity implements
        AdapterView.OnItemLongClickListener, ExpandableListView.OnGroupClickListener {
    //
    Cursor cc, c = null;
    ExpandableListView listView;
    CursorTreeAdapter adapter;
    String date_B, date_E;
    TextView textViewTotalSumm;

    private final static int ID_EDIT_SUMM = 0;
    private final static int ID_DELETE_SUMM = 1;

    private long select_ID = -1;
    private int select_ID_Parent = -1;
    private String name_category = "";
    private String summ_category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses_detail_all);

        Intent intent = getIntent();
        date_B = intent.getStringExtra("DATE_B");
        date_E = intent.getStringExtra("DATE_E");

        listView = (ExpandableListView) findViewById(R.id.listView_exp_detail_all);
        listView.setOnItemLongClickListener(this);
        listView.setOnGroupClickListener(this);
        textViewTotalSumm = (TextView) findViewById(R.id.text_total_money);
        LoadAllDataDetail(-1);
    }


    private void LoadAllDataDetail(int ExpandParent) {
        c = Constant.dbHelper.getAllDataDetailParent(date_B, date_E);
        startManagingCursor(c);
        if (c != null) {
            if (c.moveToFirst()) {
                adapter = new DBCursorAdapterViewExpDetailAll(this, c, date_B, date_E);
                listView.setAdapter(adapter);
            } else {
                setContentView(R.layout.fragment_detail_empty);
            }
            // *******************************************
            cc = Constant.dbHelper.getTotalAllDataDetail(date_B, date_E);
            if (cc.moveToFirst()) {
                try {
                    int idColIndex = cc.getColumnIndex("total_sum");
                    String str;
                    do {
                        str = cc.getString(idColIndex);
                        textViewTotalSumm.setText(str + " " + getString(R.string.grn));
                    } while (cc.moveToNext());
                } catch (Exception e) {
                    Log.e("myLogs", e.toString());
                }
                cc.close();
            } else {
                setContentView(R.layout.fragment_detail_empty);
            }

            if (ExpandParent > -1) {
                listView.expandGroup(select_ID_Parent, true);
            }
        } else {
            setContentView(R.layout.fragment_detail_empty);
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        // Implement this method to scroll to the correct position as this doesn't
        // happen automatically if we override onGroupExpand() as above
//        parent.smoothScrollToPosition(groupPosition);

        // Need default behaviour here otherwise group does not get expanded/collapsed
        // on click
        if (parent.isGroupExpanded(groupPosition)) {
            parent.collapseGroup(groupPosition);
        } else {
            parent.expandGroup(groupPosition);
        }
        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            //лонгклик был на child'е
            registerForContextMenu(parent);
            openContextMenu(parent);

            TextView text_summ_category = (TextView) view.findViewById(R.id.text_name_exp_detail_summ);
            if (text_summ_category != null) summ_category = text_summ_category.getText().toString();

            int groupPosition = listView.getPackedPositionGroup(id);
            int childPosition = listView.getPackedPositionChild(id);

            select_ID = childPosition;

            final long packedPosition = listView.getExpandableListPosition(position);
            select_ID_Parent = ExpandableListView.getPackedPositionGroup(packedPosition);
            name_category = adapter.getGroup(select_ID_Parent).getString(1);

            Log.d("myLog", "onChildClick position = " + position + ", id = " + id +
                    ", groupPosition = " + groupPosition +
                    ", childPosition = " + childPosition +
                    ", s = " + name_category);
            return true;
        } else {
            //лонгклик был на группе
            int groupPosition = listView.getPackedPositionGroup(id);
            Log.d("myLog", "onChildClick groupPosition = " + groupPosition + " id = " + id);
            return true;
        }
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
                                LoadAllDataDetail(select_ID_Parent);
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
                builder_del.setMessage(getText(R.string.menu_delete_summ_message) + " " +
                        name_category + " (" + summ_category + getText(R.string.grn) + ")  ?");
                builder_del.setCancelable(false);
                builder_del.setPositiveButton(getText(R.string.menu_delete_summ),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                if (Constant.dbHelper.delete_Summ(select_ID) > 0) {
                                    LoadAllDataDetail(select_ID_Parent);
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
