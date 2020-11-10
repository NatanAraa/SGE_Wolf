package com.example.simple_game_engine;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity
{
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);

    }

    class GameView extends SurfaceView implements Runnable
    {
        int width;
        int height;
        int Xmax;
        int i = 0;
        float timer;
        boolean isFlip = false;
        float flip = 1.0f;
        // This is our thread
        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing;

        Canvas canvas;
        Paint paint;

        long fps;

        private long timeThisFrame;

        Bitmap bitmapBob[] =
        {
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_000), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_001), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_002), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_003), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_004), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_005), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_006), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wolf_walk_007), 120, 120, false)
        };

        boolean isMoving = false;

        float walkSpeedPerSecond = 300;

        float bobXPosition = 10;

        public GameView(Context context)
        {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            playing = true;

        }

        @Override
        public void run()
        {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            width = size.x;
            height = size.y;
            Xmax = getResources().getDisplayMetrics().widthPixels;

            while (playing)
            {
                long startFrameTime = System.currentTimeMillis();
                update();
                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;

                if (timeThisFrame > 0)
                {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update()
        {
            if (isMoving)
            {
                timer += timeThisFrame;
                if (timer > 60)
                {
                    i++;
                    timer = 0;
                }
                bobXPosition = bobXPosition + (walkSpeedPerSecond / fps);

                if (bobXPosition >= Xmax - 80 || bobXPosition <= 0)
                {
                    walkSpeedPerSecond *= -1;
                    isFlip = true;
                }
                else
                {
                    isFlip = false;
                }

                if (i == bitmapBob.length)
                {
                    i = 0;
                }
            }
        }

        public void draw()
        {
            if (ourHolder.getSurface().isValid())
            {
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.argb(255, 26, 128, 182));

                paint.setColor(Color.argb(255, 249, 129, 0));

                paint.setTextSize(45);

                canvas.drawText("FPS:" + fps, 20, 40, paint);

                Bitmap bob = flipImage(bitmapBob[i]);

                canvas.drawBitmap(bob, bobXPosition, 200, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public Bitmap flipImage(Bitmap source)
        {
            Matrix matrix = new Matrix();
            if (isFlip)
                flip *= -1.0f;
            matrix.preScale(flip, 1.0f);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        public void pause()
        {
            playing = false;
            try
            {
                gameThread.join();
            } catch (InterruptedException e)
            {
                Log.e("Error:", "joining thread");
            }

        }

        public void resume()
        {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent)
        {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:

                    isMoving = true;

                    break;

                case MotionEvent.ACTION_UP:

                    isMoving = false;

                    break;
            }
            return true;
        }

    }

    protected void onResume()
    {
        super.onResume();

        gameView.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        gameView.pause();
    }
}