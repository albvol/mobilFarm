package com.example.albertovolpe.mobilfarm.UtilityClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

/**
 * Created by albertovolpe on 19/05/16.
 */
public final class UIManager {

    //Display Size
    private static DisplayMetrics displayMetrics;
    public static int screenHeight, screenWidth;

    //Animation
    public static Animation fadeIn, fadeOut;
    public static AccelerateDecelerateInterpolator accelerateDecelerate;
    public static LinearOutSlowInInterpolator linearOutSlowInInterpolator;

    public UIManager(Context context){

        FontsOverride.setDefaultFont(context, "SERIF", "Helvetica Neue Light.ttf");

        displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        //fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        //fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        accelerateDecelerate = new AccelerateDecelerateInterpolator();
        linearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
    }

    public static int dpToPixel(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static int getScreenHeight(){
        return screenHeight;
    }

    public static int getScreenWidth(){
        return screenWidth;
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels , int w , int h , boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR  ) {

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        //make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels*densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        //draw rectangles over the corners we want to be square
        if (squareTL ){
            canvas.drawRect(0, h/2, w/2, h, paint);
        }
        if (squareTR ){
            canvas.drawRect(w/2, h/2, w, h, paint);
        }
        if (squareBL ){
            canvas.drawRect(0, 0, w/2, h/2, paint);
        }
        if (squareBR ){
            canvas.drawRect(w/2, 0, w, h/2, paint);
        }


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0,0, paint);

        return output;
    }
}
