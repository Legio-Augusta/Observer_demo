package littlewing.flyone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.littlewing.flyone.app.R;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by dungnv on 11/6/14.
 */
public class Wall implements Observer {
    private int width = 42;
    private int height = 42;

    private Point box_pos;

    Bitmap box_img[] = new Bitmap[1]; // TODO, can use many for store color, level ?

    public Wall(int x, int y, Point board, int scale, Context context) {
        this.box_pos = new Point(x, y);

        Resources mRes = context.getResources();
        this.box_img[0] = BitmapFactory.decodeResource(mRes, R.drawable.orange);

        for(int i=0; i <= 0; i++) {
            this.box_img[i] = Bitmap.createScaledBitmap(this.box_img[i], getBoxSize(board.y), getBoxSize(board.y), true);       // scale box image size
        }

        setWidth(getBoxSize(board.y));
        scaleWall(scale);
    }

    public void update(Observable observable, Object data) {

    }

    public Point getPosisiton() {
        return this.box_pos;
    }

    public void setPosition(Point pos) {
        this.box_pos = pos;

    }

    public Point getBoxSize() {
        Point size = new Point(this.width, this.height);
        return size;
    }

    public void setBoxSize(Point size) {
        this.width = size.x;
        this.height = size.y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    // Set box_size by the width of screen (horizontal as height). The board devided by 25 time for boundary
    // and some wall area.
    public int getBoxSize(int mHeight) {
        return mHeight*10/12 / 25; // chia ra 25 o phan box playing,
        // screen width co vien ngoai 1/12 moi ben.
    }

    public Bitmap[] getBoxImage() { // getter for box sprite bitmap[]
        return this.box_img;
    }

    // Box can be height by 1/4 or 5/4 original height
    public void scaleWall(int ratio) {
        if(this.width * this.height > 0) {
            this.height = getHeight()*ratio/4;
            this.box_img[0] = Bitmap.createScaledBitmap(this.box_img[0], getWidth(), getHeight(), true);       // scale box image size
        } else {
            // Can't init width, height --> use 42 as default
            this.height = 42*ratio/4;
            this.width = 42;
            this.box_img[0] = Bitmap.createScaledBitmap(this.box_img[0], 42, 42*ratio/4, true);       // scale box image size
        }
    }

    // Get current sprite image of box, return Bitmap
    public Bitmap getCurrentSprite() {
        return this.box_img[0];
    }

}
