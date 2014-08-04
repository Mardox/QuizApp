package biz.modernapps.quizapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.io.InputStream;

import biz.modernapps.quizapp.Utils.MenuFunctions;


public class ResultsActivity extends Activity {

    RelativeLayout resultLL;

    TextView retryBT;
    TextView moreBT;
    TextView titleTV;
    TextView descriptionTV;
    TextView levelTV;
    TextView scoreTV;
    TextView highScoreTV;

    ImageView backgroundIV;

    int playerScore;
    int timerTime;
    int timerCount;

    private InterstitialAd interstitial;

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            playerScore = extras.getInt("EXTRA_PLAYER_SCORE");
            timerTime = extras.getInt("EXTRA_TIMER_TIME");
            timerCount = extras.getInt("EXTRA_TIMER_COUNT");
        }

        resultLL = (RelativeLayout) findViewById(R.id.result_layout);
        retryBT = (TextView) findViewById(R.id.retry_text_view);
        moreBT = (TextView) findViewById(R.id.exit_text_view);


        titleTV = (TextView) findViewById(R.id.result_title_text_view);
        descriptionTV = (TextView) findViewById(R.id.description_text_view);
        levelTV = (TextView) findViewById(R.id.level_text_view);
        scoreTV = (TextView) findViewById(R.id.current_score_text_view);
        highScoreTV = (TextView) findViewById(R.id.highest_score_text_view);

        backgroundIV = (ImageView) findViewById(R.id.result_background_image);

        initAdmobInterstitial();


        SharedPreferences storage =
                this.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_MULTI_PROCESS );
        SharedPreferences.Editor editor = storage.edit();

        int highestScore = storage.getInt(HomeActivity.PREFS_HIGH_SCORE, 0);

        if(playerScore > highestScore){
            titleTV.setText(R.string.new_record);
            editor.putInt(HomeActivity.PREFS_HIGH_SCORE, playerScore );
            editor.commit();
        }else{
            titleTV.setText(R.string.not_as_good);
        }


        if (playerScore > 0){
            levelTV.setText(HomeActivity.images.get((playerScore - 1)).getName().substring(2));


            try
            {
                // get input stream
                InputStream ims = getAssets().open(HomeActivity.images.get(playerScore-1).getPath());
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                backgroundIV.setImageDrawable(d);
            }
            catch(IOException ex)
            {
                return;
            }

        }else{
            titleTV.setText(getString(R.string.not_good_enough_text));
            levelTV.setText(R.string.negative_score_description);
            descriptionTV.setText("");
        }



        scoreTV.setText(String.valueOf(playerScore * 1000) + "XP");
        highScoreTV.setText("Best\n" + String .valueOf(highestScore * 1000) + "XP");

        retryBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (interstitial.isLoaded()){
                    displayInterstitial();
                }else{
                    initNewGame();
                }


            }
        });

        moreBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MenuFunctions.openMore(context);
                //Show interestitial and close the app
                //TODO: Add store listing here
            }
        });



    }


    private void initAdmobInterstitial(){
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.admob_challenge_interstitial));

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);

        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                initNewGame();
            }



        });
    }


    // Invoke displayInterstitial() when you are ready to display an interstitial.
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }


    private void initNewGame(){

        Intent challengeIntent = new Intent(this , ChallengeActivity.class);
        challengeIntent.putExtra("EXTRA_DIFFICULTY", HomeActivity.difficulty);

        this.startActivity(challengeIntent);
        finish();

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
            }
            // return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.results, menu);
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
