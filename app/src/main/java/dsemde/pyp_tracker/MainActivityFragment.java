package dsemde.pyp_tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    static Date temp_curr_date = Calendar.getInstance().getTime();

    static SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    static String sel_date = df.format(temp_curr_date);
    String curr_date = df.format(temp_curr_date);

    double daily_total;
    int progress = 0;
    static double daily_goal;

    TextView textView1;
    TextView textView2;
    TextView textViewFlights;
    ProgressBar pb;

    static List<ExampleItem> mExampleList;
    static List<ExampleItem> filteredList;

    ImageView previousImage;
    ImageView nextImage;

    Date beginning;
    Date end;

    static View MainActivityFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        MainActivityFragment = inflater.inflate(R.layout.fragment, container,false);
        return MainActivityFragment;
    }


    @Override
    public void onResume(){
        super.onResume();
        getCurrentDate();
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            beginning = df.parse("01-JUN-2019");
            end = df.parse("31-JUL-2019");
        } catch (ParseException e) {
            e.printStackTrace();
        }

// ----- LOAD SAVED ARRAY LIST -----
        loadData();
        loadDailyGoal();

// ----- SET VARIABLES -----
        daily_total = totalOutput(mExampleList, sel_date);
        textView1 = getView().findViewById(R.id.total);
        textView2 = getView().findViewById(R.id.daily_total);
        textViewFlights = getView().findViewById(R.id.flights);
        pb = getView().findViewById(R.id.progress_bar);

        textView1.setText(String.valueOf(daily_total));
        pb.setProgress(getProgress(mExampleList, sel_date), true);

        previousImage = getView().findViewById(R.id.previous_day);
        nextImage = getView().findViewById(R.id.next_day);

// ----- SET DAY NAVIGATION BUTTONS -----
        setNavigationImages();


// ----- BUILD RECYCLERVIEW -----
        buildRecyclerView();
        filter(sel_date);

// ----- ADD STEPS DIALOGUE -----
        setAddStepButton();

// ----- CALENDAR DIALOGUE -----
        setDateChangeButton();
}


    public double totalOutput(List<ExampleItem> steps, String date) {
        try{
            int temp_total = 0;
            double flight_total;
            for (int a = 0; a < steps.size(); a++) {
                if (date.equals(steps.get(a).getText1()))
                temp_total += steps.get(a).getText2();
            }
            flight_total = round(temp_total / 16.0, 2);
            return flight_total;
        } catch (Exception e){
            return 0.0;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int toInt(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        return bd.intValue();
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();


    }

    private void saveData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mExampleList);
        editor.putString("task list", json);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<ExampleItem>>() {}.getType();
        mExampleList = gson.fromJson(json, type);

        if (mExampleList == null){
            mExampleList = new ArrayList<>();
        }
    }

    public void loadDailyGoal(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String temp_daily_goal = sharedPref.getString("daily goal", "3.0");

        daily_goal = Double.valueOf(temp_daily_goal);
    }

    public int getProgress(List<ExampleItem> steps, String date){
        int daily_progress_int;
        try{
            int temp_progress = 0;
            double flight_total;
            for (int a = 0; a < steps.size(); a++) {
                if (date.compareTo(steps.get(a).getText1()) == 0)
                    temp_progress += steps.get(a).getText2();
            }
            flight_total = round(temp_progress / 16.0, 2);
            daily_progress_int = toInt((flight_total/daily_goal)*100);
            return daily_progress_int;
        } catch (Exception e){
            return 0;
        }
    }

    private void addProgress(double x, int prog){
        int daily_progress_int = toInt((x/daily_goal)*100);

        if (progress <= 100-daily_progress_int){
            progress = progress + prog;
            pb.setProgress(daily_progress_int, true);
        } else if (progress + daily_progress_int > 100){
            pb.setProgress(100, true);
        }

    }

    private void removeProgress(double x, int prog){
        int daily_progress_int = toInt((x/daily_goal)*100);
        progress = progress - prog;
        if (progress <= 100) {
            pb.setProgress(daily_progress_int, true);
        } else {
            pb.setProgress(0, true);

        }
    }

    public void addItem(String date, int steps, String ts) {
        mExampleList.add(new ExampleItem(date, steps, ts));
        filter(sel_date);
    }

    public void removeItem(int position) {
        String test = filteredList.get(position).getTimeStamp();

        for (ExampleItem item : mExampleList) {
            if (test.equals(item.getTimeStamp())) {
                position = mExampleList.indexOf(item);
            }
        }

        mExampleList.remove(position);
        mAdapter.notifyDataSetChanged();
        filter(sel_date);
        daily_total = totalOutput(mExampleList, sel_date);
        textView1.setText(String.valueOf(daily_total));

        if (daily_total == 1.0){
            textViewFlights.setText("flight");
        } else {
            textViewFlights.setText("flights");
        }

        removeProgress(daily_total, progress);
        saveData();
    }

    public void editItem(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.edit_steps, (ViewGroup) getView().findViewById(android.R.id.content), false);

        // Step input
        final EditText input = viewInflated.findViewById(R.id.edit_input);
        builder.setView(viewInflated);

        // OK Button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().length() != 0) {
                    try {
                        int in = Integer.parseInt(String.valueOf(input.getText()));
                        if (in > 0) {
                            String test = filteredList.get(position).getTimeStamp();
                            for (ExampleItem item : mExampleList) {
                                if (test.equals(item.getTimeStamp())) {
                                    mExampleList.get(mExampleList.indexOf(item)).setText2(in);
                                }
                            }
                        } else {
                            dialog.cancel();
                        }
                    } catch (Exception e) {
                        dialog.cancel();
                    }

                    daily_total = totalOutput(mExampleList, sel_date);
                    textView1.setText(String.valueOf(daily_total));
                    addProgress(daily_total, progress);
                    mAdapter.notifyDataSetChanged();
                    filter(sel_date);

                    if (daily_total == 1.0){
                        textViewFlights.setText("flight");
                    } else {
                        textViewFlights.setText("flights");
                    }

                    saveData();
                } else{
                    dialog.cancel();
                }

            }
        });
        // Cancel Button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                input.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        input.requestFocus();

        builder.show();
    }

    public void buildRecyclerView(){
        mRecyclerView = getView().findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new ExampleAdapter(mExampleList, getContext(), this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void filter(String text){
        filteredList = new ArrayList<>();

        for (ExampleItem item : mExampleList){
            if (item.getText1().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
    }

    public void setAddStepButton(){
        FloatingActionButton fab = getView().findViewById(R.id.addSteps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.add_steps, (ViewGroup) getView().findViewById(android.R.id.content), false);

                // Step input
                final EditText input = viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);

                // OK Button
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().length() != 0) {
                            try {
                                int in = Integer.parseInt(String.valueOf(input.getText()));
                                if (in > 0) {
                                    String timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
                                    addItem(sel_date, in, timeStamp);
                                    dialog.dismiss();
                                } else {
                                    dialog.cancel();
                                }
                            } catch (Exception e) {
                                dialog.cancel();
                            }

                            daily_total = totalOutput(mExampleList, sel_date);
                            textView1.setText(String.valueOf(daily_total));
                            addProgress(daily_total, progress);
                            mAdapter.notifyDataSetChanged();
                            filter(sel_date);

                            if (daily_total == 1.0){
                                textViewFlights.setText("flight");
                            } else {
                                textViewFlights.setText("flights");
                            }

                            saveData();
                        } else{
                            dialog.cancel();
                        }

                    }

                });



                // Cancel Button
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        input.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    }
                });

                input.requestFocus();

                builder.show();
            }
        });
    }

    public void setDateChangeButton(){
        FloatingActionButton fabcal = getView().findViewById(R.id.calendarButton);
        fabcal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout ll= (LinearLayout)inflater.inflate(R.layout.calendar, null, false);
                CalendarView cv = (CalendarView) ll.getChildAt(0);

                long milliseconds = 0;
                try {
                    Date d = df.parse(sel_date);
                    milliseconds = d.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                cv.setDate(milliseconds);
                cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(
                            @NonNull CalendarView view,
                            int year,
                            int month,
                            int dayOfMonth)
                    {
                        Date temp_sel_date = getDate(year, month, dayOfMonth);
                        sel_date = df.format(temp_sel_date);

                        if (sel_date.equals(curr_date)){
                            textView2.setText("Today");
                        } else {
                            String dt_day = (String) DateFormat.format("dd",   temp_sel_date);
                            String dt_month  = (String) DateFormat.format("MMM",  temp_sel_date);
                            textView2.setText(dt_month + " " + dt_day);
                        }

                        daily_total = totalOutput(mExampleList, sel_date);
                        textView1.setText(String.valueOf(daily_total));
                        pb.setProgress(getProgress(mExampleList, sel_date), true);
                        mAdapter.notifyDataSetChanged();
                        filter(sel_date);

                        nextImage.setVisibility(View.VISIBLE);
                        previousImage.setVisibility(View.VISIBLE);

                        if (temp_sel_date.compareTo(beginning) == 0){
                            previousImage.setVisibility(View.INVISIBLE);
                        } else if (temp_sel_date.compareTo(end) == 0){
                            nextImage.setVisibility(View.INVISIBLE);
                        }

                    }
                });

                new AlertDialog.Builder(getActivity())
                        .setView(ll)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }
                        ).show();
            }
        });
    }

    public void setNavigationImages(){
        previousImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Date temp_date;
                String dt;
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(df.parse(sel_date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, -1);
                temp_date = c.getTime();
                dt = df.format(c.getTime());
                sel_date = dt;

                if (sel_date.equals(curr_date)) {
                    textView2.setText("Today");
                } else {
                    String dt_day = (String) DateFormat.format("dd", temp_date);
                    String dt_month = (String) DateFormat.format("MMM", temp_date);
                    textView2.setText(dt_month + " " + dt_day);
                }

                daily_total = totalOutput(mExampleList, sel_date);
                textView1.setText(String.valueOf(daily_total));
                pb.setProgress(getProgress(mExampleList, sel_date), true);
                mAdapter.notifyDataSetChanged();
                filter(sel_date);

                nextImage.setVisibility(View.VISIBLE);

                if (temp_date.compareTo(beginning) == 0) {
                    previousImage.setVisibility(View.INVISIBLE);
                } else {
                    previousImage.setVisibility(View.VISIBLE);
                }
            }
        });

        nextImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Date temp_date;
                String dt;
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(df.parse(sel_date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 1);
                temp_date = c.getTime();
                dt = df.format(c.getTime());

                    sel_date = dt;

                    if (sel_date.equals(curr_date)){
                        textView2.setText("Today");
                    } else {
                        String dt_day = (String) DateFormat.format("dd",   temp_date);
                        String dt_month  = (String) DateFormat.format("MMM",  temp_date);
                        textView2.setText(dt_month + " " + dt_day);
                    }

                    daily_total = totalOutput(mExampleList, sel_date);
                    textView1.setText(String.valueOf(daily_total));
                    pb.setProgress(getProgress(mExampleList, sel_date), true);
                    mAdapter.notifyDataSetChanged();
                    filter(sel_date);

                    previousImage.setVisibility(View.VISIBLE);

                    if (temp_date.compareTo(end) == 0){
                        nextImage.setVisibility(View.INVISIBLE);
                    } else{
                        nextImage.setVisibility(View.VISIBLE);
                    }
            }
        });
    }

    public void getCurrentDate(){
        Date filler_date = null;
        Date temp_curr_date_b = Calendar.getInstance().getTime();
        curr_date = df.format(temp_curr_date_b);

        if (temp_curr_date_b.after(end)){
            sel_date = df.format(end);

            try {
                filler_date = df.parse(sel_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dt_day = (String) DateFormat.format("dd",   filler_date);
            String dt_month  = (String) DateFormat.format("MMM",  filler_date);
            textView2.setText(dt_month + " " + dt_day);
            nextImage.setVisibility(View.INVISIBLE);

        } else if (temp_curr_date_b.before(beginning)){
            sel_date = df.format(beginning);

            try {
                filler_date = df.parse(sel_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dt_day = (String) DateFormat.format("dd",   filler_date);
            String dt_month  = (String) DateFormat.format("MMM",  filler_date);
            textView2.setText(dt_month + " " + dt_day);
            previousImage.setVisibility(View.INVISIBLE);
        } else if (sel_date.equals(curr_date)){
            textView2.setText("Today");
        }

        daily_total = totalOutput(mExampleList, sel_date);
        textView1.setText(String.valueOf(daily_total));
        pb.setProgress(getProgress(mExampleList, sel_date), true);
        mAdapter.notifyDataSetChanged();
        filter(sel_date);
    }
}