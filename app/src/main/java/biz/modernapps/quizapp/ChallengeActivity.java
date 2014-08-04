package biz.modernapps.quizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import biz.modernapps.quizapp.model.ImageItem;


public class ChallengeActivity extends Activity {

    public final String TAG = "QuizApp";

    ImageView mainIV;

    private InterstitialAd interstitial;

    public static ArrayList<ImageItem> imagesShuffled;

    int correctAnswer;

    int currentQuestion;

    TextView btn1;
    TextView btn2;
    TextView btn3;
    TextView btn4;

    TextView timerTV;
    TextView scoreTV;

    //default button color
    Drawable d;

    int playerScore = 0;

    CountDownTimer timer;
    int timerCount = 0;
    int timerTime = 0;

    int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        getActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            difficulty = extras.getInt("EXTRA_DIFFICULTY");
        }

        mainIV = (ImageView) findViewById(R.id.main_image_view);
        btn1 = (TextView) findViewById(R.id.choice_button_1);
        btn2 = (TextView) findViewById(R.id.choice_button_2);
        btn3 = (TextView) findViewById(R.id.choice_button_3);
        btn4 = (TextView) findViewById(R.id.choice_button_4);


        timerTV = (TextView) findViewById(R.id.timer_text_view);
        scoreTV = (TextView) findViewById(R.id.score_text_view);

        //Get the default background color
        d = btn1.getBackground();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(0);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(1);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(2);
            }
        });


        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(3);
            }
        });



        imagesShuffled = new ArrayList<ImageItem>(HomeActivity.images);

        //Randomize the order of the images in the array
        long seed = System.nanoTime();
        Collections.shuffle(imagesShuffled, new Random(seed));


        timerTime = (3/difficulty) * 1000 * imagesShuffled.size();

        initAdmobInterstitial();

        startQuiz();

    }

    private void initAdmobInterstitial(){
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.admob_challenge_interstitial));

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);

    }


    // Invoke displayInterstitial() when you are ready to display an interstitial.
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }


    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();

    }


    @Override
    protected void onResume() {
        super.onResume();
        timer.cancel();
        startTimer(timerTime - (timerCount*1000));
    }

    public void startQuiz(){

        //Get All the file names in an array
        currentQuestion = 0;
        createQuestion();
        startTimer(timerTime);

    }


    public void  startTimer(int time){
        timer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTV.setText(getString(R.string.timer_text) +" "+ millisUntilFinished / 1000);
                timerCount++;
            }

            public void onFinish() {
                timerTV.setText("done!");
                resultsPage();
            }
        }.start();
    }

    /**
     * Override the back button
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event)
    {
        if (keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {

            if(!getString(R.string.admob_challenge_interstitial).equals("")){
                //RevMob Full Screen Ad
                displayInterstitial();
                timer.cancel();
                finish();

            }
            // return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void createQuestion(){

        int randomFileIndex;

        if(currentQuestion%10 == 0 && interstitial.isLoaded()){
            displayInterstitial();
            initAdmobInterstitial();
        }

        //picks a random number for the answer
        correctAnswer = 0 + (int)(Math.random() * ((3 - 0) + 1));

        //create an array of answers from file names
        ArrayList<String> answers = new ArrayList<String>();
        List<Integer> categoryList = new ArrayList<Integer>();

        for (int subArrayFlag = 0; subArrayFlag < imagesShuffled.size();subArrayFlag++){
            if(imagesShuffled.get(currentQuestion).getName().substring(0,1)
                    .equalsIgnoreCase(imagesShuffled.get(subArrayFlag).getName().substring(0, 1))){
                categoryList.add(subArrayFlag);

            }
        }

        //get 3 random answers and add it to the array
        for (int i = 0 ; i < 4 ;i++ ){

            if (i == correctAnswer){
                answers.add(imagesShuffled.get(currentQuestion).getName());
            }else {

                do {
                    randomFileIndex = (int) (Math.random() * categoryList.size());
                } while ( categoryList.get(randomFileIndex) == currentQuestion && categoryList.size() > 0 );

                answers.add(imagesShuffled.get(categoryList.get(randomFileIndex)).getName());
            }

        }



        try
        {
            // get input stream
            InputStream ims = getAssets().open(imagesShuffled.get(currentQuestion).getPath());
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            mainIV.setImageDrawable(d);
        }
        catch(IOException ex)
        {
            return;
        }


        btn1.setText(answers.get(0).substring(2));
        btn2.setText(answers.get(1).substring(2));
        btn3.setText(answers.get(2).substring(2));
        btn4.setText(answers.get(3).substring(2));



        btn1.setBackgroundDrawable(d);
        btn2.setBackgroundDrawable(d);
        btn3.setBackgroundDrawable(d);
        btn4.setBackgroundDrawable(d);



    }



    private void submitAnswer(int answer){


        if(answer == correctAnswer){
            currentQuestion++;
            playerScore++;

            if (currentQuestion == imagesShuffled.size()){
                //Show the dialog
                resultsPage();

            }else{

                switch (answer) {
                    case 0:
                        btn1.setBackgroundColor(Color.GREEN);
                        break;
                    case 1:
                        btn2.setBackgroundColor(Color.GREEN);
                        break;
                    case 2:
                        btn3.setBackgroundColor(Color.GREEN);
                        break;
                    case 3:
                        btn4.setBackgroundColor(Color.GREEN);
                        break;
                }

                final int finalAnswer = answer;

                // SLEEP 2 SECONDS HERE ...
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
//                        if (currentQuestion%2 == 0){
//                            displayInterstitial();
//
//                        }


                        createQuestion();
                    }
                }, 500);

            }
        }else{
            playerScore--;
            switch (answer) {
                case 0:
                    btn1.setBackgroundColor(Color.RED);
                    break;
                case 1:
                    btn2.setBackgroundColor(Color.RED);
                    break;
                case 2:
                    btn3.setBackgroundColor(Color.RED);
                    break;
                case 3:
                    btn4.setBackgroundColor(Color.RED);
                    break;
                default:
                    break;
            }

        }

        scoreTV.setText(getString(R.string.score_text)+playerScore);


    }



    private void resultsPage(){

        String msg = "";

        currentQuestion = 0;

        if(timerTime > timerCount){
            msg = "Congrats!";
        }

        timer.cancel();

        Intent resultIntent = new Intent(this , ResultsActivity.class);
        resultIntent.putExtra("EXTRA_PLAYER_SCORE", playerScore);
        resultIntent.putExtra("EXTRA_TIMER_COUNT", timerCount);
        resultIntent.putExtra("EXTRA_TIMER_TIME", timerTime);

        this.startActivity(resultIntent);

        finish();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
