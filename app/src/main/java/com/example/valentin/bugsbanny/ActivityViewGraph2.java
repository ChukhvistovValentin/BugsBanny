package com.example.valentin.bugsbanny;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
//import org.achartengine.chartdemo.demo.R;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityViewGraph2 extends Activity {
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    Cursor c = null;
    int count_category = -1;

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        mSeries = (CategorySeries) savedState.getSerializable("current_series");
        mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("current_series", mSeries);
        outState.putSerializable("current_renderer", mRenderer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graph2);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(0);
//        mRenderer.setDisplayValues(true);
        mRenderer.setLegendTextSize(24);
        mRenderer.setLabelsTextSize(24f);

        Intent intent = getIntent();
        String date_b = intent.getStringExtra("DATE_B");
        String date_e = intent.getStringExtra("DATE_E");
        String order = intent.getStringExtra("ORDER");
        getmChartView();
        loadFromDBDataCategory(date_b, date_e, order);
//        mAdd.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                double value = 0;
//                try {
//                    value = Double.parseDouble(mValue.getText().toString());
//                } catch (NumberFormatException e) {
//                    mValue.requestFocus();
//                    return;
//                }
//                mSeries.add("Series " + (mSeries.getItemCount() + 1), value);
//                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
//                renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
//                mRenderer.addSeriesRenderer(renderer);
//                mChartView.repaint();
//            }
//        });
    }

    private void loadFromDBDataCategory(String date_B, String date_E, String order) {
        c = Constant.dbHelper.getAllDataGraph(date_B, date_E, order);
        if (c != null) {
            if (c.moveToFirst()) {
                count_category = c.getCount();
                int c_summ = c.getColumnIndex("summ");
                int c_name = c.getColumnIndex(Constant.dbHelper.COLUMN_TXT);
                int c_color = c.getColumnIndex(Constant.dbHelper.COLUMN_COLOR);
                do {
                    double value = 0;
                    try {
                        value = Double.parseDouble(c.getString(c_summ));//mValue.getText().toString());
                    } catch (NumberFormatException e) {
                        return;
                    }
                    mSeries.add(c.getString(c_name), value);
                    SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                    renderer.setColor(c.getInt(c_color));
                    mRenderer.addSeriesRenderer(renderer);
//
//                    category_summ.add(i, c.getFloat(c_summ));
//                    column_name.add(i, c.getString(c_name));
//                    color_category.add(i, c.getInt(c_color));
                } while (c.moveToNext());
            }
            mChartView.repaint();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getmChartView();
    }

    private void getmChartView() {
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
//                        Toast.makeText(ActivityViewGraph2.this, "No chart element selected", Toast.LENGTH_SHORT)
//                                .show();
                    } else {
                        for (int i = 0; i < mSeries.getItemCount(); i++) {
                            mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
                        }
                        mChartView.repaint();
                        TextView mName = (TextView) findViewById(R.id.coordinate1);

                        mName.setText(mSeries.getCategory(seriesSelection.getPointIndex()));
//                        mName.setTextColor(mSeries.  color_category.get((int) dataPoint.getX()));

                        TextView mSumm = (TextView) findViewById(R.id.coordinate2);
                        Double d = mSeries.getValue(seriesSelection.getPointIndex());//(int) seriesSelection.getValue()
                        mSumm.setText(String.format("%.2f" + " " + getText(R.string.grn), d));
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }
}
