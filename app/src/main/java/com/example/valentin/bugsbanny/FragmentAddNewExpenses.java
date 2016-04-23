package com.example.valentin.bugsbanny;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fab.FloatingActionButton;
import com.fab.ObservableScrollView;

public class FragmentAddNewExpenses extends Fragment {
    private Cursor c = null;
    //    private View viewFragment = null;
    ObservableScrollView scrollView;
//    private int RESULT_ADD_CATEGORY = 1;
//    boolean load_data = false;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

//    public static FragmentAddNewExpenses newInstance(String param1, String param2) {
//        FragmentAddNewExpenses fragment = new FragmentAddNewExpenses();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    public FragmentAddNewExpenses() {
//        // Required empty public constructor
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        Log.d("myLog", "Fragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d("myLog", "Fragment onCreateView");
        View v = inflater.inflate(R.layout.fragment_add_new_expenses, container, false);
//        viewFragment = v;
        scrollView = (ObservableScrollView) v.findViewById(R.id.scroll_view);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.attachToScrollView(scrollView);
        setRetainInstance(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityAddCategory.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        Log.d("myLog", "Fragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        loadCategory();
    }
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
////        try {
////            mListener = (OnFragmentInteractionListener) activity;
////        } catch (ClassCastException e) {
////            throw new ClassCastException(activity.toString()
////                    + " must implement OnFragmentInteractionListener");
////        }
//        Log.d("myLog", "Fragment onAttach");
//    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
        if (c != null) c.close();
//        if (dbHelper != null) {
//            dbHelper.close();
//        }
//        scrollView = null;
//        Log.d("myLog", "Fragment onDetach");
    }

    public void loadCategory() {
//        if (load_data) return;
//        Log.d("myLog", "Fragment loadCategory");
        c = Constant.dbHelper.getAllData();
        getActivity().startManagingCursor(c);
        if (c != null) {
            if (c.moveToFirst()) {
                // определяем номера столбцов по имени в выборке
                int idColIndex = c.getColumnIndex("_id");
                int nameColIndex = c.getColumnIndex("category_name");
                do {
                    addButton(c.getString(nameColIndex), c.getInt(idColIndex));
                } while (c.moveToNext());
            }
        }
//        load_data = true;
    }

    private void addButton(String caption, int id) {
//        Log.d("myLog", "Fragment addButton  :" + id);
        LinearLayout sahne = (LinearLayout) getActivity().findViewById(R.id.container_d);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //********************
        Button btn = new Button(getActivity());
        btn.setId(id++);
        btn.setText(caption);
        btn.setLayoutParams(lp);

        LinearLayout LinLay = new LinearLayout(getActivity());
        LinLay.setPadding(20, 30, 20, 0);
        ViewGroup.LayoutParams linLayPar = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        LinLay.setLayoutParams(linLayPar);

        int pic = Constant.get_Picture_Index_Default(id - 1);
        btn.setCompoundDrawablesWithIntrinsicBounds(pic, 0, 0, 0);
        btn.setOnClickListener(oclBtnOk);
        LinLay.addView(btn);
        LayoutAnimationController controller = AnimationUtils
                .loadLayoutAnimation(getActivity(), R.anim.list_layout_controller_button);
        LinLay.setLayoutAnimation(controller);
//        ImageViewAnimatedChange(getActivity(), btn, pic);
        sahne.addView(LinLay);
    }

    View.OnClickListener oclBtnOk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Log.d("myLog", "Fragment oclBtnOk");
            Button btn = (Button) getActivity().findViewById(v.getId());
            String category_name = btn.getText().toString();
            Intent intent = new Intent(getActivity(), ActivityAddNewExpenses.class);
            intent.putExtra("CATEGORY", category_name);
            intent.putExtra("ID_T", v.getId());
            startActivity(intent);
        }
    };


}
