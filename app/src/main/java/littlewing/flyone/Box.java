package littlewing.flyone;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.widget.Toast;

import com.littlewing.flyone.app.R;

/**
 * Created by dungnv on 11/6/14.
 */

public class Box extends Observable {
    private int width;
    private int height;
    private int box_x;
    private int box_y;

    private Point box_pos;

    private int box_velocity;
    Bitmap box_img[] = new Bitmap[5];
    private int box_idx;			// index for bomb image

    public Box(int x, int y, Context context) {
        this.box_x = x;
        this.box_y = y;

        Resources mRes = context.getResources();
        box_img[0] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue);
        box_img[1] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue_90);
        box_img[2] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue_180);
        box_img[3] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue_270);
    }

    public Point getPosisiton() {
        return this.box_pos;
    }

    public void setPosition(Point pos) {
        this.box_pos = pos;

        setChanged();
        notifyObservers();
    }

}
