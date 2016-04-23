package com.example.valentin.bugsbanny;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBCursorAdapterViewExpDetailAll extends CursorTreeAdapter {
    LayoutInflater mLayoutInflater;
    private LayoutInflater mInflater_group;

    private String date_b;
    private String date_e;

    public DBCursorAdapterViewExpDetailAll(Context context, Cursor cursor, String date_b, String date_e) {

        super(cursor, context);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.date_b = date_b;
        this.date_e = date_e;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        mInflater_group = LayoutInflater.from(context);
        final View view = mInflater_group.inflate(R.layout.row_exp_detail_all_category, parent, false);
        return view;
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        final View childView = mLayoutInflater.inflate(R.layout.row_exp_detail_all_category_child, parent, false);
        return childView;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        TextView name_group = (TextView) view.findViewById(R.id.text_name_category);
        String s = cursor.getString(cursor.getColumnIndex(Constant.dbHelper.COLUMN_TXT));
        name_group.setText(s);
        //SUMM *******
        TextView text_summ_category = (TextView) view.findViewById(R.id.text_summ_category);
        s = cursor.getString(cursor.getColumnIndex(Constant.dbHelper.COLUMN_SUMM));
        text_summ_category.setText("(" + s + ")");
        // IMAGE********
        ImageView im = (ImageView) view.findViewById(R.id.image_exp_detail_all_category);
        int num = cursor.getColumnIndex(Constant.dbHelper.DB_TABLE_T_ID);
        int id = cursor.getInt(num);
        im.setImageResource(Constant.get_Picture_Index_Default(id));
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        TextView dateChild = (TextView) view.findViewById(R.id.text_date_exp_detail_all_category);
        TextView summ = (TextView) view.findViewById(R.id.text_name_exp_detail_summ);

        summ.setText(cursor.getString(cursor.getColumnIndex(Constant.dbHelper.COLUMN_SUMM)));
        String d = cursor.getString(cursor.getColumnIndex(Constant.dbHelper.DB_TABLE_DATE));
        try {
            Date date;
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
            date = formatter.parse(d);

            String mask = new SimpleDateFormat("dd MMMM yyyy \nhh:mm:ss").format(date);
            dateChild.setText(mask);
        } catch (Exception e) {
            dateChild.setText(d);
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return super.getGroupId(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return super.getChildId(groupPosition, childPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
    }

    @Override
    public void setGroupCursor(Cursor cursor) {

    }

    @Override
    public Cursor getChildrenCursor(Cursor groupCursor) {
        int id = groupCursor.getInt(groupCursor.getColumnIndex("_id"));
        return Constant.dbHelper.getAllDataDetailChild(id, date_b, date_e);
    }

    @Override
    public Cursor getGroup(int groupPosition) {
        return super.getGroup(groupPosition);
    }
}
