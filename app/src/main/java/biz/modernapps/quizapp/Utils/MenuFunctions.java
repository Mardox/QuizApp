package biz.modernapps.quizapp.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import biz.modernapps.quizapp.R;

/**
 * Created by Hooman on 31/07/2014.
 */
public class MenuFunctions {

    public static boolean openMore(Context context){

        Intent intent = new Intent(Intent.ACTION_VIEW);
        //Try Google play
        intent.setData(Uri.parse("market://developer?id=" + context.getResources().getString(R.string.developer)));
        if (MyStartActivity(intent, context) == false) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id="+context.getResources().getString(R.string.developer)));
            if (MyStartActivity(intent, context) == false) {
                //Well if this also fails, we have run out of options, inform the user.
                Toast.makeText(context, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private static boolean MyStartActivity(Intent aIntent, Context context) {
        try
        {
            context.startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }
}
