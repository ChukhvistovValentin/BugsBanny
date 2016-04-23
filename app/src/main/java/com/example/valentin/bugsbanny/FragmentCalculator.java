package com.example.valentin.bugsbanny;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;


public class FragmentCalculator extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

    Spinner lvMonth;
    Spinner lvYear;
    Button btn_calendar;
    ArrayList<String> year_arr = new ArrayList<String>();
    TextView textViewTotalSumm;
    ListView listView;
    Cursor cc, c = null;

    FrameLayout cont1;
    FrameLayout cont2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_calculator, container, false);
        try {
            btn_calendar = (Button) v.findViewById(R.id.btn_calendar);
            btn_calendar.setOnClickListener(onClickCalendar);
            cont1 = (FrameLayout) v.findViewById(R.id.cont1);
            cont2 = (FrameLayout) v.findViewById(R.id.cont2);

            setVisibleList(true);
            listView = (ListView) v.findViewById(R.id.listView_calculator);

            textViewTotalSumm = (TextView) v.findViewById(R.id.text_total_money);
            lvMonth = (Spinner) v.findViewById(R.id.spinner_month);
            lvYear = (Spinner) v.findViewById(R.id.spinner_year);

//            final ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
//                    getActivity(), R.array.names_month, android.R.layout.simple_spinner_item);
            String[] m = getResources().getStringArray(R.array.names_month);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, android.R.id.text1, m);

            adapter.setDropDownViewResource(R.layout.row_spinner_month);
            //android.R.layout.simple_spinner_dropdown_item);
            lvMonth.setAdapter(adapter);
//            if (Constant.getMonth_index() < Constant.getCurrentMonth())
            lvMonth.setSelection(Constant.getCurrentMonth());
//            else
//            lvMonth.setSelection(Constant.getMonth_index());
//            Toast.makeText(getActivity(), "month onCreateView = " + Constant.getMonth_index(), Toast.LENGTH_LONG).show();
            lvMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    String s = lvYear.getSelectedItem().toString();
//                    adapter.notifyDataSetChanged();
//                    updateYearArr(Integer.valueOf(s));
//                    lvMonth.setSelection(position - 1);
                    Constant.setMonth_index(position);
//                    Toast.makeText(getActivity(), "month onItemSelected = " + Constant.getMonth_index()
//                            + "\nposition = " + position
//                            + "\nid = " + id, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
//                    lvMonth.setSelection(Constant.getCurrentMonth());
//                    Toast.makeText(getActivity(), "month onNothingSelected = " + Constant.getMonth_index(), Toast.LENGTH_LONG).show();
                }
            });

            updateYearArr(0);
            final ArrayAdapter<String> adapter_y = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, android.R.id.text1, year_arr);
            adapter_y.setDropDownViewResource(R.layout.row_spinner_month);
            //android.R.layout.simple_spinner_dropdown_item);
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

        } catch (Exception e) {
        }

        viewListData();
//        Toast.makeText(getActivity(), "month = " + Constant.getCurrentMonth(), Toast.LENGTH_LONG).show();
        return v;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (c != null) c.close();
        if (cc != null) cc.close();
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

    private void viewListData() {
        int year, month, day;
        year = Integer.valueOf(lvYear.getSelectedItem().toString());
        if (Constant.getMonth_index() > -1)
            month = Constant.getMonth_index(); //
        else
            month = lvMonth.getSelectedItemPosition();
        day = 1;
        Date date = new Date(year, month, day);
        loadFromDBDataCalculator(setDateToStr(date, true), setDateToStr(date, false));
//        Toast.makeText(getActivity(), "month = " + month, Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(), "month = " + Constant.getCurrentMonth(), Toast.LENGTH_LONG).show();
    }

    View.OnClickListener onClickCalendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_calendar:
                    viewListData();
            }
        }
    };

    private void loadFromDBDataCalculator(String date_B, String date_E) {
        c = Constant.dbHelper.getAllDataCalculator(date_B, date_E);
        getActivity().startManagingCursor(c);
        if (c != null) {
            if (c.moveToFirst()) {
                try {
                    SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                            R.layout.row_calculate, c,
                            new String[]{"_id", "summ", Constant.dbHelper.COLUMN_TXT},
                            new int[]{R.id.icon_calculator, R.id.text_calc_summ, R.id.text_calc_name});

                    adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                            if (view.getId() == R.id.icon_calculator) {
                                int num = c.getColumnIndex("_id");
                                int id = c.getInt(num);
                                ((ImageView) view).setImageResource(Constant.get_Picture_Index_Default(id));
                                return true;
                            }
                            return false;
                        }
                    });
                    setVisibleList(true);
                    listView.setAdapter(adapter);
                    LayoutAnimationController controller = AnimationUtils
                            .loadLayoutAnimation(getActivity(), R.anim.list_layout_controller);
                    listView.setLayoutAnimation(controller);
                } catch (Exception e) {
                    Log.e("myLog", e.toString());
                }
            } else {
                setVisibleList(false);
                listView.setAdapter(null);
            }
            // *******************************************
            cc = Constant.dbHelper.getAllDataCalculatorTotal(date_B, date_E);
            if (cc != null) {
                if (cc.moveToFirst()) {
                    try {
                        int idColIndex = cc.getColumnIndex("summ");
                        String str;
                        do {
                            str = cc.getString(idColIndex);
                            str = (str == null) ? "0" : str;
                            textViewTotalSumm.setText(str + " " + getString(R.string.grn));
                        } while (cc.moveToNext());
                    } catch (Exception e) {
                        Log.e("myLog", e.toString());
                    }
                    cc.close();
                } else {
                    textViewTotalSumm.setText("0 " + getString(R.string.grn));
                }
            }
        } else {
//            setVisibleData(false);
//            listView.setAdapter(null);
        }
    }

//    private void setVisibleData(boolean visibleData) {
//        Fragment frag;
//        if (visibleData) {
//            FragmentTransaction tran = getFragmentManager().beginTransaction();
//            tran.replace(R.id.cont, frag_lv, "frag_lv");
//            tran.commit();
////            tran.add(R.id.cont, frag_lv);
////            tran = getFragmentManager().beginTransaction();
////            getFragmentManager().beginTransaction().remove(frag_empty);
//            //**********
////            frag = frag_lv;
//            Fragment fr = getFragmentManager().findFragmentById(R.id.cont);
//            if (fr != null)
//                listView = (ListView) fr.getView().findViewById(R.id.listView_calculator);
//            else
//                listView = (ListView) getActivity().findViewById(R.id.listView_calculator);
//        } else {
//
//            FragmentTransaction tran = getFragmentManager().beginTransaction();
//            tran.replace(R.id.cont, frag_empty, "frag_empty");
////            tran.addToBackStack(null);
//            tran.commit();
////            getFragmentManager().beginTransaction().remove(frag_lv);
////            frag = frag_empty;
//        }
//
////        getFragmentManager().beginTransaction().replace(R.id.cont, frag).commit();
//    }

    private void setVisibleList(boolean visibleList) {
        if (visibleList) {
            cont1.setVisibility(View.VISIBLE);
            cont2.setVisibility(View.INVISIBLE);
        } else {
            cont1.setVisibility(View.INVISIBLE);
            cont2.setVisibility(View.VISIBLE);
        }
//        try {
//            Fragment fragmentList = (Fragment) getFragmentManager().findFragmentById(R.id.cont);
//            Fragment fragmentEmpty = (Fragment) getFragmentManager().findFragmentById(R.id.cont2);
//
//            if (visibleList) {
//                fragmentList.getView().setVisibility(View.VISIBLE);
//                fragmentEmpty.getView().setVisibility(View.INVISIBLE);
//            } else {
//                fragmentList.getView().setVisibility(View.INVISIBLE);
//                fragmentEmpty.getView().setVisibility(View.VISIBLE);
//            }
//        } catch (Exception e) {
//
//        }
    }


}
