package com.example.valentin.bugsbanny;

import android.app.ActionBar.Tab;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
    //implements ActionBar.TabListener
    //ActionBar.OnNavigationListener,
    private static String ACTION_BAR_INDEX = "ACTION_BAR_INDEX";

    final String LOG_TAG = "myLogs";
    private static final String FRAGMENT_SELECTED_ADD_EXP = "item_add_axp";
    private static final String FRAGMENT_SELECTED_DIAGRAM = "item_diagram";
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private MyTabListener<FragmentAddNewExpenses> t1;
    MyTabListener<FragmentCalculator> t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.d("myLog", "MainActivity onCreate");
        Constant.createDBHelper(this);
        final ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
//        actionBar.setListNavigationCallbacks(
//                // Specify a SpinnerAdapter to populate the dropdown list.
//                new ArrayAdapter<String>(
//                        actionBar.getThemedContext(),
//                        android.R.layout.simple_list_item_1,
//                        android.R.id.text1,
//                        new String[]{
//                                getString(R.string.title_section1),
//                                getString(R.string.title_section2),
//                                getString(R.string.title_section3),
//                        }),
//                this);
//        String label1 = getResources().getString(R.string.title_section1);
//        Fragment fragment_add_new_exp = new FragmentAddNewExpenses();
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(R.string.add_expenses).setIcon(R.drawable.new_flow24);

        t1 = new MyTabListener<FragmentAddNewExpenses>(this,
                R.id.content_main, FragmentAddNewExpenses.class);
        tab.setTabListener(t1);        //new FragmentTabListener(this, fragment_add_new_exp));
        try {
            actionBar.addTab(tab);
        } catch (Exception e) {
            Log.d("myLog", "MainActivity onCreateTab = " + e.toString());
        }
//****************************************************************************
//        String label2 = getResources().getString(R.string.title_section2);
//        Fragment fragment_add_category = new FragmentCalculator();
        tab = actionBar.newTab();
        tab.setText(R.string.calc).setIcon(R.drawable.calc24);
        t2 = new MyTabListener<FragmentCalculator>(this,
                R.id.content_main, FragmentCalculator.class);
        tab.setTabListener(t2);//new FragmentTabListener(this, fragment_add_category));
        actionBar.addTab(tab);

    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        // Restore the previously serialized current dropdown position.
//        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
//            getActionBar().setSelectedNavigationItem(
//                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
//        }
//    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        // Serialize the current dropdown position.
//        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
//                getActionBar().getSelectedNavigationIndex());
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();

        switch (id) {
            case R.id.item2:
                intent = new Intent(MainActivity.this, ActivityViewExpenses.class);
                startActivity(intent);
                return true;
            case R.id.item3:
                intent = new Intent(MainActivity.this, ActivityPaintGraphView.class);
                startActivity(intent);
                return true;
            case R.id.item4:
                intent = new Intent(MainActivity.this, Preferences.class);
                startActivity(intent);
                return true;
            case R.id.item5:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onNavigationItemSelected(int position, long id) {
//        // When the given dropdown item is selected, show its contents in the
//        // container view.
//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
//        return true;
//    }


//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//    }

//    @Override
//    public void onTabReselected(Tab tab, FragmentTransaction ft) {
//        Log.d(LOG_TAG, "reselected tab: " + tab.getText());
//    }
//
//    @Override
//    public void onTabSelected(Tab tab, FragmentTransaction ft) {
//        Log.d(LOG_TAG, "selected tab: " + tab.getText());
//    }
//
//    @Override
//    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
//        Log.d(LOG_TAG, "unselected tab: " + tab.getText());
//    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        View fragmentContainer = findViewById(R.id.content_main);
        boolean tabletLayout = fragmentContainer == null;
        if (!tabletLayout) {
// Сохраните выбор текущей вкладки на Панели действий,
            int actionBarIndex = getActionBar().getSelectedTab().getPosition();
            SharedPreferences.Editor editor =
                    getPreferences(Activity.MODE_PRIVATE).edit();
            editor.putInt(ACTION_BAR_INDEX, actionBarIndex);
            editor.apply();
// Отсоедините каждый из Фрагментов.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (t1.getmFragment() != null)
                ft.detach(t1.getmFragment());
            if (t2.getmFragment() != null)
                ft.detach(t2.getmFragment());
            ft.commit();
        }
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedlnstanceState) {
        super.onRestoreInstanceState(savedlnstanceState);
        View fragmentContainer = findViewById(R.id.content_main);
        boolean tabletLayout = fragmentContainer == null;
        if (!tabletLayout) {
            t1.setmFragment(getFragmentManager().findFragmentByTag(FragmentAddNewExpenses.class.getName()));
            t2.setmFragment(getFragmentManager().findFragmentByTag(FragmentCalculator.class.getName()));
            SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
            int actionBarIndex = sp.getInt(ACTION_BAR_INDEX, 0);
            getActionBar().setSelectedNavigationItem(actionBarIndex);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View fragmentContainer = findViewById(R.id.content_main);
        boolean tabletLayout = fragmentContainer == null;
        if (!tabletLayout) {
            SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
            int actionBarIndex = sp.getInt(ACTION_BAR_INDEX, 0);
            getActionBar().setSelectedNavigationItem(actionBarIndex);
        }
    }


}
