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

    private int rotation; // goc quay cua box
    private int gravity = 3;
    private boolean isJumping; // box jumping or not
    private boolean isDead; // box die or live

    private Point box_pos;

    private int box_velocity;
    Bitmap box_img[] = new Bitmap[5];
    private int box_idx;			// index for box sprite images

    public Box(int x, int y, Point board, Context context) {
        this.box_pos = new Point(x, y);

        Resources mRes = context.getResources();
        box_img[0] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue);
        box_img[1] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue_90); // box rotated 90 degrees
        box_img[2] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue_180);
        box_img[3] = BitmapFactory.decodeResource(mRes, R.drawable.box_blue_270);
        box_img[4] = BitmapFactory.decodeResource(mRes, R.drawable.effect_09);

        for(int i=0; i <= 3; i++) {
            box_img[i] = Bitmap.createScaledBitmap(box_img[i], getBoxSize(board.y), getBoxSize(board.y), true);       // scale box image size
        }

        this.box_idx = 0; // Init sprite 0
        this.isJumping = false;
        this.rotation = 0;
    }

    public Point getPosisiton() {
        return this.box_pos;
    }

    public void setPosition(Point pos) {
        this.box_pos = pos;

        // notify observer
        setChanged();
        notifyObservers();
    }

    public int getBoxIdx() {
        return this.box_idx;
    }

    public void setBoxIdx(int index) {
        this.box_idx = index;
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

    public int getHeight() {
        return this.height;
    }

    // Set box_size by the width of screen (horizontal as height). The board devided by 25 time for boundary
    // and some wall area.
    public int getBoxSize(int mHeight) {
        return mHeight*10/12 / 25; // chia ra 25 o phan box playing,
        // screen width co vien ngoai 1/12 moi ben.
    }

    // Now only set square, so both width, height is the same
    public void setBoxSize(int mHeight) {
        this.width = this.height = mHeight*10/12/25;
    }

    public Bitmap[] getBoxImage() { // getter for box sprite bitmap[]
        return this.box_img;
    }

    // Get current sprite image of box, return Bitmap
    public Bitmap getCurrentSprite() {
        return this.box_img[this.box_idx];
    }

    public void setExplode() {
        this.box_idx = 4;
    }

    // Box move toward
    public void boxMoveX(int step) {
        this.box_pos.x += step;
    }

    // box jump upward
    public void boxMoveY(int step) {
        this.box_pos.y += step;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void rotate(int angle) {
        this.rotation += angle;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
}
