package dsemde.pyp_tracker;


public class ExampleItem {
    private String mText1;
    private int mText2;
    private String mTimeStamp;

    ExampleItem(String text1, int text2, String timeStamp){
        mText1 = text1;
        mText2 = text2;
        mTimeStamp = timeStamp;
    }

    String getText1(){
        return mText1;
    }

    public void setText1(String date){
        mText1 = date;
    }

    int getText2(){
        return mText2;
    }

    void setText2(int steps){
        mText2 = steps;
    }

    String getTimeStamp(){
        return mTimeStamp;
    }

    public void setTimeStamp (String timeStamp){
        mTimeStamp = timeStamp;
    }

}
