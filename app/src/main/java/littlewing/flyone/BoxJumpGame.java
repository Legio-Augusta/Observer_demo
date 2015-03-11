package littlewing.flyone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.littlewing.flyone.app.R;

/**
 * Created by dungnv on 12/19/14.
 */
public class BoxJumpGame {
    /**
     * State-tracking constants.
     */
    public static final int STATE_START = -1;
    public static final int STATE_PLAY = 0;
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_RUNNING = 3;

    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = -1;

    private int yVel = 0;
    private int characterGround = 480; // TODO

    public static final int BOX_STEP = 6; // box run 10px per step

    // used to calculate level for mutes and trigger clip
    public int mHitStreak = 0;

    // total number asteroids you need to hit.
    public int mHitTotal = 0;

    // which music bed is currently playing?
    public int mCurrentBed = 0;

    // a lazy graphic fudge for the initial title splash
    public Bitmap mTitleBG;

    public Bitmap mTitleBG2;

    public boolean mInitialized = false;

    /** Queue for GameEvents */
    protected ConcurrentLinkedQueue<GameEvent> mEventQueue = new ConcurrentLinkedQueue<GameEvent>();

    /** Context for processKey to maintain state accross frames * */
    protected Object mKeyContext = null;

    // the timer display in seconds
    public int mTimerLimit;

    // used for internal timing logic.
    public final int TIMER_LIMIT = 72;

    // string value for timer display
    private String mTimerValue = "1:12";

    // start, play, running, lose are the states we use
    public int mState;

    // has laser been fired and for how long?
    // user for fx logic on laser fire
    boolean mLaserOn = true;

    long mLaserFireTime = 0;

    private final byte TIMER_EVENT = 82;

    // used to track beat for synch of mute/unmute actions
    private int mBeatCount = 1;

    // used to save the beat event system time.
    private long mLastBeatTime;

    private long mPassedTime;

    // how much do we move the asteroids per beat?
    private int mPixelMoveX = 25;

    // the asteroid send events are generated from the Jet File.
    // but which land they start in is random.
    private Random mRandom = new Random();

    private boolean mJetPlaying = false;

    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = false;

    // updates the screen clock. Also used for tempo timing.
    private Timer mTimer = null;

    private TimerTask mTimerTask = null;

    // one second - used to update timer
    public int mTaskIntervalInMillis = 1000;

    private int mCanvasHeight = 1;

    private int mCanvasWidth = 1;

    // used to track the picture to draw for ship animation
    private int mShipIndex = 0;

    public Vector<Explosion> mExplosion;

    // screen width, height
    private int mWidth = 720; //(int)getWidth();
    private int mHeight = 1280; //(int)getHeight();

    // how far up (close to top) jet boy can fly
    public int mJetBoyYMin = mWidth/3*2; //40;
    public int mJetBoyX = (int)mWidth/2; //0;
    public int mJetBoyY = (int)mHeight*3/4; //0;

    public static final String TAG = "BoxJump";

    // Direction moving
    public int MOVE_DIR = 0; // moving dir

    public Context context;
    Point board = new Point(mWidth, mHeight);
    private Box myBox;
    private Wall myWall;

    public BoxJumpGame() {
        super();
    }

    public BoxJumpGame(Context context) {
        super();
        this.context = context;
        myBox = new Box(mJetBoyX, mJetBoyY, board, context);
        myWall = new Wall(mJetBoyX, mJetBoyY, board, 6, context); // scale 6/4
    }

    public Bitmap[] loadBeam(Bitmap[] mBeam, Context mContext) {
        Resources mRes = mContext.getResources();
        mBeam[0] = BitmapFactory.decodeResource(mRes, R.drawable.effect_10); //intbeam_1
        mBeam[1] = BitmapFactory.decodeResource(mRes, R.drawable.effect_11);
        mBeam[2] = BitmapFactory.decodeResource(mRes, R.drawable.effect_12);
        mBeam[3] = BitmapFactory.decodeResource(mRes, R.drawable.effect_12);

        return mBeam;
    }

    public Bitmap[] loadExplosion(Bitmap[] mExplosions, Context mContext) {
        Resources mRes = mContext.getResources();

        mExplosions[0] = BitmapFactory.decodeResource(mRes, R.drawable.effect_07);
        mExplosions[1] = BitmapFactory.decodeResource(mRes, R.drawable.effect_08);
        mExplosions[2] = BitmapFactory.decodeResource(mRes, R.drawable.effect_09);
        mExplosions[3] = BitmapFactory.decodeResource(mRes, R.drawable.effect_09);

        return mExplosions;
    }

    public Bitmap[] loadBaseLine(Bitmap[] mLine, Context mContext) {
        Resources mRes = mContext.getResources();

        mLine[0] = BitmapFactory.decodeResource(mRes, R.drawable.base_line);
        mLine[0] = Bitmap.createScaledBitmap(mLine[0], 1080, 8, true);

        return mLine;
    }

    public void setJetPlaying(boolean bool) {
        this.mJetPlaying = bool;
    }

    public int getCanvasWidth() {
        return this.mCanvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.mCanvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return this.mCanvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.mCanvasHeight = canvasHeight;
    }

    public void setScreenWidth(int scr_width) {
        this.mWidth = scr_width;
    }

    public void setLastBeatTime(long time) {
        this.mLastBeatTime = time;
    }

    public int getTIMER_EVENT() {
        return this.TIMER_LIMIT;
    }

    public Random getRandom() {
        return this.mRandom;
    }

    public void setRun(boolean bl) {
        this.mRun = bl;
    }

    public boolean getRun() {
        return this.mRun;
    }

    public TimerTask getTimerTask() {
        return this.mTimerTask;
    }

    public void setTimerTask(TimerTask tmt) {
        this.mTimerTask = tmt;
    }

    public void setShipIndex(int idx) {
        this.mShipIndex = idx;
    }

    public int getShipIndex() {
        return this.mShipIndex;
    }

    public void setTimerValue(String timer) {
        this.mTimerValue = timer;
    }

    public String getTimerValue() {
        return this.mTimerValue;
    }

    public Timer getTimer() {
        return this.mTimer;
    }

    public void setTimer(Timer timer) {
        this.mTimer = timer;
    }

    public long getLastBeatTime() {
        return mLastBeatTime;
    }

    public boolean ismJetPlaying() {
        return mJetPlaying;
    }

    public boolean getJetPlaying() {
        return this.mJetPlaying;
    }

    public long getPassedTime() {
        return mPassedTime;
    }

    public void setPassedTime(long mPassedTime) {
        this.mPassedTime = mPassedTime;
    }

    public int getBeatCount() {
        return mBeatCount;
    }

    public void setBeatCount(int mBeatCount) {
        this.mBeatCount = mBeatCount;
    }

    public void drawBoxRunning(Canvas canvas) {
        Point curPos = myBox.getPosisiton();
        myBox.boxMoveX(BOX_STEP);
        Log.e(TAG, "box-x " + curPos.x + " box-y: " +curPos.y);

        Matrix matrix = new Matrix();

        if (myBox.isJumping()) {

            myBox.rotate(10);
            float px = curPos.x + myBox.getBoxSize(mHeight)/2;
            float py = curPos.y + myBox.getBoxSize(mHeight)/2;
            matrix.postTranslate(-myBox.getCurrentSprite().getWidth()/2, -myBox.getCurrentSprite().getHeight()/2);

            matrix.postRotate(myBox.getRotation()); // Quay 180 hay 90
            matrix.postTranslate(px, py);
            canvas.drawBitmap(myBox.getCurrentSprite(), matrix, null);

            myBox.boxMoveX(3);   // Hieu chinh toc do box chay ngang
            // Neu set o box_step thi lam cho box chay qua nhanh
            //TODO tim nguyen nhan lieu co phai do dong bo sync time khong.
            // yVel to boxUpward()

            yVel += myBox.getGravity();

            myBox.setPosition(new Point(curPos.x, (curPos.y+=yVel) ));

            if (curPos.y > characterGround) {  // Box chim qua sau --> cho noi len mat nc
                curPos.y = characterGround;
                myBox.setPosition(new Point(curPos.x, curPos.y));
                yVel = 0;
                myBox.setJumping(false);
            }

            // reset time to 0. If want independence - > use function.
        } else {
            matrix.postTranslate(curPos.x, curPos.y);
            canvas.drawBitmap(myBox.getCurrentSprite(), matrix, null);
        }

        if(curPos.x >= getBounce()) {
            curPos.x = getStartLeft();
            myBox.setPosition(new Point(curPos.x, curPos.y));
            // win, new level
        }

    }

    // In some game Engine, this task done by TWEEN
    // It will do thing in amount of TIME
    // like jumping A to B in 2 seconds
    // The height of jumping is another variable (body.velocity.y) and is dependence
    // engine auto compute angle when jump based on the height of jump
    // TODO apply this task

    public void jump() {
        if (myBox.isJumping() == false) {
            yVel = -30;
            myBox.setJumping(true);
        }
    }
    public void drawWall(Canvas canvas, Wall wall) {
        if(wall != null) {
            Log.e(TAG, "wall_height " + wall.getHeight() + " pos " + wall.getPosisiton().y + " ground " + characterGround);

            canvas.drawBitmap(wall.getCurrentSprite(), wall.getPosisiton().x, wall.getPosisiton().y - wall.getHeight() + getBoxSize(), null);
        }
    }

    public void drawGreateWall(Canvas canvas) {
        ArrayList<Wall> level1 = getLevel();

        for (Wall temp: level1) {
            drawWall(canvas, temp);
        }
    }

    public void drawBaseLine(Canvas line, Bitmap[] mLine) {
        line.drawBitmap(mLine[0], getStartLeft(), characterGround+getBoxSize(), null);
    }

    // TODO ko biet co fai mHeight ko, vi dang quay ngang
    private int getBounce() {
        return mHeight*11/12 - 2*getBoxSize(); // scrn_width chia lam 12 phan --> can phai 1 phan con 11.
        // bo o cuoi cung cach ra. Do canvas ve start_x, y Top-Right nen cach 1 o nua.
    }

    public int getBoxSize() {
        return mHeight*10/12 / 25; // chia ra 25 o phan box playing,
        // screen width co vien ngoai 1/12 moi ben.
    }

    private int getStartLeft() {
        return mHeight/12; // scrn_width chia lam 12 phan --> can phai 1 phan con 11.
    }

    public ArrayList<Wall> getLevel() {
        ArrayList<Wall> level1 = new ArrayList<Wall>();
        level1.add(new Wall(mJetBoyX-2*getBoxSize(), mJetBoyY, board, 4, context));
        level1.add(new Wall(mJetBoyX+5*getBoxSize(), mJetBoyY, board, 6, context));
        level1.add(new Wall(mJetBoyX+6*getBoxSize(), mJetBoyY, board, 2, context));
        level1.add(new Wall(mJetBoyX+12*getBoxSize(), mJetBoyY, board, 8, context));
        level1.add(new Wall(mJetBoyX+17*getBoxSize(), mJetBoyY, board, 3, context));

        return level1;
    }

    public void collision(ArrayList<Wall> greateWall, Box myBox) {
        for (Wall temp: greateWall) {
            Point checkPoint = new Point(temp.getWidth(), 0);
            Point range = new Point(temp.getWidth(), 0);
            if(inRange(myBox, checkPoint, ))
        }
    }

    // Check if number x in a range
    public boolean inRange(int xCheck, int x, int range) {
        if((xCheck <= x+range) && (xCheck >= x)) {
            return true;
        }
        return false;
    }

    // Check box colidate rectangle
    public boolean rectangleColidate(Box checkBox, Point wall_top_left, Point range) {
        boolean inRangeX = inRange(checkBox.getPosisiton().x, wall_top_left.x, range.x); // range.x is box_width
        boolean inRangeY = inRange(checkBox.getPosisiton().y, wall_top_left.y - checkBox.getWidth(), range.y); // top-left box need some mod, range.y equal 0

        if(inRangeX && inRangeY) {
            return true;
        }
        return false;
    }
}
