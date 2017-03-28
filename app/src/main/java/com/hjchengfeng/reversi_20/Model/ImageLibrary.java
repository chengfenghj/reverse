package com.hjchengfeng.reversi_20.Model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hjchengfeng.reversi_20.R;

/**
 * Created by ASUS on 2016/12/30.
 */
public class ImageLibrary {

    private Bitmap[] chesses =new Bitmap[20];
    private Bitmap background;

    public ImageLibrary(Resources res){
        loadBitmap(res);
    }
    private void loadBitmap(Resources res){
        background =BitmapFactory.decodeResource(res, R.drawable.beijing);
        chesses[0] =BitmapFactory.decodeResource(res, R.drawable.black);
        chesses[1] =BitmapFactory.decodeResource(res, R.drawable.shadow1);
        chesses[2] =BitmapFactory.decodeResource(res, R.drawable.shadow2);
        chesses[3] =BitmapFactory.decodeResource(res, R.drawable.shadow3);
        chesses[4] =BitmapFactory.decodeResource(res, R.drawable.shadow4);
        chesses[5] =BitmapFactory.decodeResource(res, R.drawable.shadow5);
        chesses[6] =BitmapFactory.decodeResource(res, R.drawable.shadow6);
        chesses[7] =BitmapFactory.decodeResource(res, R.drawable.shadow7);
        chesses[8] =BitmapFactory.decodeResource(res, R.drawable.shadow8);
        chesses[9] =BitmapFactory.decodeResource(res, R.drawable.shadow9);
        chesses[10] =BitmapFactory.decodeResource(res, R.drawable.white);
        chesses[11] =BitmapFactory.decodeResource(res, R.drawable.shadow11);
        chesses[12] =BitmapFactory.decodeResource(res, R.drawable.shadow12);
        chesses[13] =BitmapFactory.decodeResource(res, R.drawable.shadow13);
        chesses[14] =BitmapFactory.decodeResource(res, R.drawable.shadow14);
        chesses[15] =BitmapFactory.decodeResource(res, R.drawable.shadow15);
        chesses[16] =BitmapFactory.decodeResource(res, R.drawable.shadow16);
        chesses[17] =BitmapFactory.decodeResource(res, R.drawable.shadow17);
        chesses[18] =BitmapFactory.decodeResource(res, R.drawable.shadow18);
        chesses[19] =BitmapFactory.decodeResource(res, R.drawable.shadow19);
    }
    public Bitmap getBackground(){
        return background;
    }
    public Bitmap getChesses(int index){
        return chesses[index];
    }
}
