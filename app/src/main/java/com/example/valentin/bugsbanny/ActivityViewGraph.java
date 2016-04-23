package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import java.util.ArrayList;

public class ActivityViewGraph extends Activity {
    Cursor c = null;
    int count_category = -1;
    double xInterval = 1.0;
    ArrayList<Integer> color_category = new ArrayList<Integer>();
    ArrayList<Float> category_summ = new ArrayList<Float>();
    ArrayList<String> column_name = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graph);
        Intent intent = getIntent();
        String date_b = intent.getStringExtra("DATE_B");
        String date_e = intent.getStringExtra("DATE_E");
        String order = intent.getStringExtra("ORDER");

        loadFromDBDataCategory(date_b, date_e, order);
        DataPoint[] points = null;
        if (count_category > 0) {
            points = new DataPoint[count_category];
            for (int i = 0; i < count_category; i++) {
                points[i] = new DataPoint(i, category_summ.get(i));
            }
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle(getString(R.string.view_expenses));

        if (points == null) {
            points = new DataPoint[0];
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(points);
        graph.addSeries(series);

        int spacing = count_category * 5;
        if (spacing == 0) spacing = 20;

        series.setSpacing(spacing); //20
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);

        graph.getViewport().setXAxisBoundsManual(true);
        if (series instanceof BarGraphSeries) {
            graph.getViewport().setMinX(series.getLowestValueX() - (xInterval / 2.0));
            graph.getViewport().setMaxX(series.getHighestValueX() + (xInterval / 2.0));
        } else {
            graph.getViewport().setMinX(series.getLowestValueX());
            graph.getViewport().setMaxX(series.getHighestValueX());
        }

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                int reColor;
                if (count_category > -1) {
                    reColor = color_category.get((int) data.getX());
                } else {
                    reColor = Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
                return reColor;
            }
        });
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                TextView mName = (TextView) findViewById(R.id.coordinate1);
                mName.setText(column_name.get((int) dataPoint.getX()));
                mName.setTextColor(color_category.get((int) dataPoint.getX()));

                TextView mSumm = (TextView) findViewById(R.id.coordinate2);
                mSumm.setText(String.format("%.2f" + " " + getText(R.string.grn), category_summ.get((int) dataPoint.getX())));
            }
        });
    }

    private void loadFromDBDataCategory(String date_B, String date_E, String order) {
        c = Constant.dbHelper.getAllDataGraph(date_B, date_E, order);
        if (c != null) {
            if (c.moveToFirst()) {
                count_category = c.getCount();
                int c_summ = c.getColumnIndex("summ");
                int c_name = c.getColumnIndex(Constant.dbHelper.COLUMN_TXT);
                int c_color = c.getColumnIndex(Constant.dbHelper.COLUMN_COLOR);
                int i = 0;
                do {
                    category_summ.add(i, c.getFloat(c_summ));
                    column_name.add(i, c.getString(c_name));
                    color_category.add(i, c.getInt(c_color));
                    i++;
                } while (c.moveToNext());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null) c.close();
    }
}
