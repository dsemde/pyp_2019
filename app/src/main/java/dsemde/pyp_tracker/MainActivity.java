package dsemde.pyp_tracker;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static dsemde.pyp_tracker.MainActivityFragment.daily_goal;
import static dsemde.pyp_tracker.MainActivityFragment.mExampleList;
import static dsemde.pyp_tracker.MainActivityFragment.sel_date;
import static dsemde.pyp_tracker.MainActivityFragment.toInt;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

    double weekOneTotal = 0.0;
    double weekTwoTotal = 0.0;
    double weekThreeTotal = 0.0;
    double weekFourTotal = 0.0;

    double weekOneMeters = 0.0;
    double weekTwoMeters = 0.0;
    double weekThreeMeters = 0.0;
    double weekFourMeters = 0.0;

    Date startDateWeek1;
    Date endDateWeek1;
    Date startDateWeek2;
    Date endDateWeek2;
    Date startDateWeek3;
    Date endDateWeek3;
    Date startDateWeek4;
    Date endDateWeek4;

    {
        try {
            startDateWeek1 = df.parse("17-JUN-2019");
            endDateWeek1 = df.parse("23-JUN-2019");
            startDateWeek2 = df.parse("24-JUN-2019");
            endDateWeek2 = df.parse("30-JUN-2019");
            startDateWeek3 = df.parse("01-JUL-2019");
            endDateWeek3 = df.parse("07-JUL-2019");
            startDateWeek4 = df.parse("08-JUL-2019");
            endDateWeek4 = df.parse("12-JUL-2019");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    TextView weekOneTextView;
    TextView weekTwoTextView;
    TextView weekThreeTextView;
    TextView weekFourTextView;

    TextView weekOneMetersView;
    TextView weekTwoMetersView;
    TextView weekThreeMetersView;
    TextView weekFourMetersView;

    TextView dailyGoal;
    TextView challengeTotal;

    ImageView startDot;
    ImageView diamondHead;
    ImageView burnabyMountain;
    ImageView stawamusChief;
    ImageView tableMountain;
    ImageView grouseMountain;
    ImageView cypressBowl;
    ImageView mountOlympus;
    ImageView mountStHelens;
    ImageView mountFuji;
    ImageView mountKilimanjaro;
    ImageView mountEverest;

    TextView textDiamondHead;
    TextView textBurnabyMountain;
    TextView textStawamusChief;
    TextView textTableMountain;
    TextView textGrouseMountain;
    TextView textCypressBowl;
    TextView textMountOlympus;
    TextView textMountStHelens;
    TextView textMountFuji;
    TextView textMountKilimanjaro;
    TextView textMountEverest;

    ProgressBar pb;
    ProgressBar summary_progress;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        FragmentPageAdapter sectionsPagerAdapter = new FragmentPageAdapter(this, getSupportFragmentManager() );
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);



    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

        try {
            getWeeklyTotals();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getWeeklyMeters();

        setViews();

        setChallengeProgress();

        setPeakVisibility();

        getChallengeTotal();

        setDailyGoal();

        pb.setProgress(getProgress(mExampleList, sel_date), true);

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void getWeeklyTotals() throws ParseException {
        Date tempDate;

        weekOneTotal = 0.0;
        weekTwoTotal = 0.0;
        weekThreeTotal = 0.0;
        weekFourTotal = 0.0;

        for (ExampleItem item: mExampleList) {
            tempDate = df.parse(item.getText1());
            if (tempDate.after(startDateWeek1) && tempDate.before(endDateWeek1) || tempDate.equals(startDateWeek1) || tempDate.equals(endDateWeek1)) {
                weekOneTotal += item.getText2();
            } else if (tempDate.after(startDateWeek2) && tempDate.before(endDateWeek2) || tempDate.equals(startDateWeek2) || tempDate.equals(endDateWeek2)) {
                weekTwoTotal += item.getText2();
            } else if (tempDate.after(startDateWeek3) && tempDate.before(endDateWeek3) || tempDate.equals(startDateWeek3) || tempDate.equals(endDateWeek3)) {
                weekThreeTotal += item.getText2();
            } else if (tempDate.after(startDateWeek4) && tempDate.before(endDateWeek4) || tempDate.equals(startDateWeek4) || tempDate.equals(endDateWeek4)) {
                weekFourTotal += item.getText2();
            }
        }
        weekOneTotal = round(weekOneTotal/16, 1);
        weekTwoTotal = round(weekTwoTotal/16, 1);
        weekThreeTotal = round(weekThreeTotal/16, 1);
        weekFourTotal = round(weekFourTotal/16, 1);
    }

    public void getWeeklyMeters(){
        weekOneMeters = round(weekOneTotal*3.0, 1);
        weekTwoMeters = round(weekTwoTotal*3.0, 1);
        weekThreeMeters = round(weekThreeTotal*3.0, 1);
        weekFourMeters = round(weekFourTotal*3.0, 1);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void getChallengeTotal(){
        double challenge_total = 0.0;

        for (ExampleItem item: mExampleList){
            challenge_total += item.getText2();
        }

        challenge_total = round(challenge_total/16, 1);

        challengeTotal.setText(challenge_total + " flights");
    }

    public void setChallengeProgress(){
        double mtEverest = 47189.28;
        double currentProgress = 0;
        double relativeProgress;

        for (ExampleItem item: mExampleList){
            currentProgress += item.getText2();
        }

        relativeProgress = round((currentProgress/mtEverest)*100, 0);
        summary_progress.setProgress((int) relativeProgress, true);
    }

    public double getChallengeProgress(){
        double mtEverest = 47189.28;
        double currentProgress = 0;
        double relativeProgress;

        for (ExampleItem item: mExampleList){
            currentProgress += item.getText2();
        }

        relativeProgress = round((currentProgress/mtEverest)*100, 2);
        return relativeProgress;
    }

    public void setPeakVisibility() {
        double progress = getChallengeProgress();


        diamondHead.setVisibility(View.INVISIBLE);
        burnabyMountain.setVisibility(View.INVISIBLE);
        stawamusChief.setVisibility(View.INVISIBLE);
        tableMountain.setVisibility(View.INVISIBLE);
        grouseMountain.setVisibility(View.INVISIBLE);
        cypressBowl.setVisibility(View.INVISIBLE);
        mountOlympus.setVisibility(View.INVISIBLE);
        mountStHelens.setVisibility(View.INVISIBLE);
        mountFuji.setVisibility(View.INVISIBLE);
        mountKilimanjaro.setVisibility(View.INVISIBLE);
        mountEverest.setVisibility(View.INVISIBLE);

        textDiamondHead.setVisibility(View.INVISIBLE);
        textBurnabyMountain.setVisibility(View.INVISIBLE);
        textStawamusChief.setVisibility(View.INVISIBLE);
        textTableMountain.setVisibility(View.INVISIBLE);
        textGrouseMountain.setVisibility(View.INVISIBLE);
        textCypressBowl.setVisibility(View.INVISIBLE);
        textMountOlympus.setVisibility(View.INVISIBLE);
        textMountStHelens.setVisibility(View.INVISIBLE);
        textMountFuji.setVisibility(View.INVISIBLE);
        textMountKilimanjaro.setVisibility(View.INVISIBLE);
        textMountEverest.setVisibility(View.INVISIBLE);

        if (progress == 0.00){
            startDot.setVisibility(View.INVISIBLE);
        } else if (progress > 0.00 && progress < 2.62){
            startDot.setVisibility(View.VISIBLE);
        } else if (progress >= 2.62 && progress < 4.18 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            diamondHead.setVisibility(View.VISIBLE);
            textDiamondHead.setVisibility(View.VISIBLE);
        } else if (progress >= 4.18 && progress < 7.91 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            burnabyMountain.setVisibility(View.VISIBLE);
            textBurnabyMountain.setVisibility(View.VISIBLE);
        } else if (progress >= 7.91 && progress < 12.26 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            stawamusChief.setVisibility(View.VISIBLE);
            textStawamusChief.setVisibility(View.VISIBLE);
        } else if (progress >= 12.26 && progress < 13.91 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            tableMountain.setVisibility(View.VISIBLE);
            textTableMountain.setVisibility(View.VISIBLE);
        } else if (progress >= 13.91 && progress < 16.18 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            grouseMountain.setVisibility(View.VISIBLE);
            textGrouseMountain.setVisibility(View.VISIBLE);
        } else if (progress >= 16.18 && progress < 22.04 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            cypressBowl.setVisibility(View.VISIBLE);
            textCypressBowl.setVisibility(View.VISIBLE);
        } else if (progress >= 22.04 && progress < 28.82 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            mountOlympus.setVisibility(View.VISIBLE);
            textMountOlympus.setVisibility(View.VISIBLE);
        } else if (progress >= 28.82 && progress < 42.68 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            mountStHelens.setVisibility(View.VISIBLE);
            textMountStHelens.setVisibility(View.VISIBLE);
        } else if (progress >= 42.68 && progress < 66.62 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            mountFuji.setVisibility(View.VISIBLE);
            textMountFuji.setVisibility(View.VISIBLE);
        } else if (progress >= 66.62 && progress < 100.00 && progress < 100.00) {
            startDot.setVisibility(View.VISIBLE);
            mountKilimanjaro.setVisibility(View.VISIBLE);
            textMountKilimanjaro.setVisibility(View.VISIBLE);
        } else if (progress >= 100.00){
            startDot.setVisibility(View.VISIBLE);
            mountEverest.setVisibility(View.VISIBLE);
            textMountEverest.setVisibility(View.VISIBLE);
        }

    }

    public void setDailyGoal(){
        dailyGoal.setText("Personal goal: " + daily_goal + " flights per day");
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

    public void setViews(){
        weekOneTextView = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekOneTotal);
        weekOneTextView.setText(String.valueOf(weekOneTotal));
        weekTwoTextView = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekTwoTotal);
        weekTwoTextView.setText(String.valueOf(weekTwoTotal));
        weekThreeTextView  = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekThreeTotal);
        weekThreeTextView.setText(String.valueOf(weekThreeTotal));
        weekFourTextView = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekFourTotal);
        weekFourTextView.setText(String.valueOf(weekFourTotal));
        summary_progress = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.overallProgressBar);

        weekOneMetersView = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekOneMeters);
        weekOneMetersView.setText(String.valueOf(weekOneMeters));
        weekTwoMetersView = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekTwoMeters);
        weekTwoMetersView.setText(String.valueOf(weekTwoMeters));
        weekThreeMetersView  = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekThreeMeters);
        weekThreeMetersView.setText(String.valueOf(weekThreeMeters));
        weekFourMetersView = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.weekFourMeters);
        weekFourMetersView.setText(String.valueOf(weekFourMeters));

        startDot = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.startDot);
        diamondHead = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.diamondHead);
        burnabyMountain = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.burnabyMountain);
        stawamusChief = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.stawamusChief);
        tableMountain = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.tableMountain);
        grouseMountain = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.grouseMountain);
        cypressBowl = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.cypressBowl);
        mountOlympus = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.mountOlympus);
        mountStHelens = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.mountStHelens);
        mountFuji = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.mountFuji);
        mountKilimanjaro = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.mountKilimanjaro);
        mountEverest = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.mountEverest);

        textDiamondHead = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textDiamondHead);
        textBurnabyMountain = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textBurnabyMountain);
        textStawamusChief = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textStawamusChief);
        textTableMountain = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textTableMountain);
        textGrouseMountain = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textGrouseMountain);
        textCypressBowl = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textCypressBowl);
        textMountOlympus = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textMountOlympus);
        textMountStHelens = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textMountStHelens);
        textMountFuji = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textMountFuji);
        textMountKilimanjaro = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textMountKilimanjaro);
        textMountEverest = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.textMountEverest);

        challengeTotal = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.challengeTotal);

        dailyGoal = SummaryActivityFragment.SummaryFragmentView.findViewById(R.id.dailyGoal);

        pb = MainActivityFragment.MainActivityFragment.findViewById(R.id.progress_bar);
    }
}
