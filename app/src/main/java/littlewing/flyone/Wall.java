package littlewing.flyone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.littlewing.flyone.app.R;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by dungnv on 11/6/14.
 */
public class Wall implements Observer{
    private int width;
    private int height;
    private int box_x;
    private int box_y;

    private int rotation; // goc quay cua box
    private boolean isJumping; // box jumping or not

    private Point box_pos;

    Bitmap box_img[] = new Bitmap[1]; // TODO, can use many for store color, level ?

    public Wall(int x, int y, Point board, Context context) {
        this.box_pos = new Point(x, y);

        Resources mRes = context.getResources();
        box_img[0] = BitmapFactory.decodeResource(mRes, R.drawable.orange);

        for(int i=0; i <= 0; i++) {
            box_img[i] = Bitmap.createScaledBitmap(box_img[i], getBoxSize(board.y), getBoxSize(board.y), true);       // scale box image size
        }

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

    public int getBoxX() {
        return this.width;
    }

    public int getBoxY() {
        return this.height;
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

}