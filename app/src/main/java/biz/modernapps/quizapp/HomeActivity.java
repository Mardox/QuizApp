package biz.modernapps.quizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import biz.modernapps.quizapp.model.ImageItem;


public class HomeActivity extends Activity {

    public final String TAG = "QuizApp";

    ImageView mainIV;

    ArrayList<ImageItem> images;

    int correctAnswer;

    int currentQuestion;

    TextView btn1;
    TextView btn2;
    TextView btn3;
    TextView btn4;

    //default button color
    Drawable d;

    int playerScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mainIV = (ImageView) findViewById(R.id.main_image_view);
        btn1 = (TextView) findViewById(R.id.choice_button_1);
        btn2 = (TextView) findViewById(R.id.choice_button_2);
        btn3 = (TextView) findViewById(R.id.choice_button_3);
        btn4 = (TextView) findViewById(R.id.choice_button_4);

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


        ImageItem img;

        images = new ArrayList<ImageItem>();

        //list images and them to the images array
        try {
            String[] files = getAssets().list("aimages");
            for (String file : files) {
                img = new ImageItem();
                //i3.setName("Love");
                img.setName(file);
                img.setPath("aimages/"+file);
                images.add(img);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Randomize the order of the images in the array
        long seed = System.nanoTime();
        Collections.shuffle(images, new Random(seed));


        startQuiz();



    }



    private void startQuiz(){

        //Get All the file names in an array

        currentQuestion = 0;
        createQuestion();


    }


    private void createQuestion(){



        int randomFileIndex;

        //picks a random number for the answer
        correctAnswer = 0 + (int)(Math.random() * ((3 - 0) + 1));

        //create an array of answers from file names
        ArrayList<String> answers = new ArrayList<String>();

        //get 3 random answers and add it to the array
        for (int i = 0 ; i < 4 ;i++ ){

            if (i == correctAnswer){
                answers.add(images.get(currentQuestion).getName());
            }else {
                do {
                    randomFileIndex = (int) (Math.random() * images.size());
                } while (randomFileIndex == currentQuestion);

                answers.add(images.get(randomFileIndex).getName());
            }

        }



        try
        {
            // get input stream
            InputStream ims = getAssets().open(images.get(currentQuestion).getPath());
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            mainIV.setImageDrawable(d);
        }
        catch(IOException ex)
        {
            return;
        }


        btn1.setText(answers.get(0));
        btn2.setText(answers.get(1));
        btn3.setText(answers.get(2));
        btn4.setText(answers.get(3));



        btn1.setBackgroundDrawable(d);
        btn2.setBackgroundDrawable(d);
        btn3.setBackgroundDrawable(d);
        btn4.setBackgroundDrawable(d);



    }



    private void submitAnswer(int answer){


        if(answer == correctAnswer){
            currentQuestion++;
            playerScore++;

            if (currentQuestion == images.size()){
                //Show the dialog
                gameOverDialog();
                //Reset the quiz
                startQuiz();
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

                        createQuestion();
                    }
                }, 1000);

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


    }



    private void gameOverDialog(){

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.game_end_title))
                .setItems(R.array.rating_response, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                //Retry
                                startQuiz();
                                break;
                            case 1:
                                //Exit the app
                                finish();
                                //TODO:SHow interstitial
                                break;

                        }

                    }

                })
                .setIcon(R.drawable.ic_launcher)
                .show();

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
