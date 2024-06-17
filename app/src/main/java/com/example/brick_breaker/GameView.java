package com.example.brick_breaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Random;
import android.os.Handler;

public class GameView extends View{

    Context context;
    float ballX, ballY;
    Velocity velocity = new Velocity(25, 32);
    Handler handler;
    final long UPDATE_MILLIS = 30;
    //핸들러 지연 시간 30밀리초
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    Paint brickPaint = new Paint();
    float TEXT_SIZE = 120;
    float paddleX, paddleY; // 블록을 쳐내는 패달 위치를 추적하기 위한 변수
    float oldX, oldPaddleX; // 패달 위치를 위한 부동 변수
    int points = 0;
    int life = 3;
    Bitmap ball, paddle;
    int dWidth, dHeight; // 화면 높이, 너비
    int ballWidth, ballHeight; // 공 높이, 너비

    MediaPlayer mpHit, mpMiss, mpBreak;
    Random random;

    Brick[] bricks = new Brick[30]; // 피격 대상의 벽돌 최대 30개 정의
    int numBricks = 0;
    int brokenBricks = 0; // 깬 벽돌의 수
    boolean gameOver = false;

    public GameView(Context context) {
        super(context);
        this.context = context;

        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.stick);

        handler = new Handler();
        
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        //사운드 이펙트
        mpHit = MediaPlayer.create(context, R.raw.hit);
        mpMiss = MediaPlayer.create(context, R.raw.miss);
        mpBreak = MediaPlayer.create(context, R.raw.breaking);

        //점수 표기용 텍스트
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);

        //벽돌과 체력바
        healthPaint.setColor(Color.GREEN);
        brickPaint.setColor(Color.BLUE);

        // 화면 높이, 너비 계산
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;

        // 랜덤하게 공의 X의 진행 방향을 지정
        random = new Random();
        ballX = random.nextInt(dWidth - 50);
        ballY = dHeight/3;
        paddleY = (dHeight * 4)/ 5;
        paddleX = dWidth/2 - paddle.getWidth()/2;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        createBricks();


    }

    private void createBricks() { // 벽돌 24개 생성
        int brickWidth = dWidth / 8;
        int brickHeight = dHeight / 16;
        for (int column = 0; column < 8; column++)
        {
            for(int row = 0; row <3; row++)
            {
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                numBricks++;
            }
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        
        // 공의 무브먼트 생성
        ballX += velocity.getX();
        ballY += velocity.getY();
    
        // 벽에 닿았을 때 공의 움직임 방향을 전환 y는 천장에 닿았을 때
        if((ballX >= dWidth - ball.getWidth()) || ballX <= 0)
        {
            velocity.setX(velocity.getX() * -1);
        }
        if(ballY <= 0)
        {
            velocity.setY(velocity.getY() * -1);
        }
        // 패달이 공을 놓쳤을 때.
        if (ballY > paddleY + paddle.getHeight()) {
            // 좌우 진행 방향을 무작위화.
            ballX = 1 + random.nextInt(dWidth - ball.getWidth() - 1);
            ballY = dHeight / 3;
            
            // 미스 사운드 재생
            if (mpMiss != null) {
                mpMiss.start();
            }
            // x값 속도 제어
            velocity.setX(xVelocity());
            velocity.setY(32);
            life--;
            if (life < 0) {
                gameOver = true;
                GameOver();
            }
        }
        // 페달이 공을 쳤을 때
            if(((ballX + ball.getWidth()) >= paddleX) &&
                    (ballX <= paddleX + paddle.getWidth()) &&
                    (ballY + ball.getHeight() >= paddleY) &&
                    (ballY + ball.getHeight() <= paddleY + paddle.getHeight()))
            {
                if (mpHit != null)
                {
                    mpHit.start();
                }
                velocity.setX(velocity.getX() + 1);
                velocity.setY((velocity.getY() + 1) * -1);
            }
            canvas.drawBitmap(ball, ballX, ballY, null);
            canvas.drawBitmap(paddle, paddleX, paddleY, null);
        
            //벽돌을 캔버스에 그리기
            for(int i = 0; i < numBricks; i++)
            {
                if(bricks[i].getVisibility())
                {
                    canvas.drawRect(bricks[i].col * bricks[i].width + 1,
                            bricks[i].row * bricks[i].heigth + 1,
                            bricks[i].col * bricks[i].width + bricks[i].width -1,
                            bricks[i].row * bricks[i].heigth + bricks[i].heigth - 1, brickPaint);
                }
            }
            canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
            // 체력은 총 4개 3, 2, 1, 0. 각 초, 노, 빨, 검.
            if(life == 2)
            {
                healthPaint.setColor(Color.YELLOW);
            }
            else if(life == 1)
            {
                healthPaint.setColor(Color.RED);
            }
            else if(life == 0)
            {
                healthPaint.setColor(Color.BLACK);
            }
            else
            {
                healthPaint.setColor(Color.GREEN);
            }

            canvas.drawRect(dWidth-200, 30, dWidth - 200 + 60 * life, 80, healthPaint);

            for(int i = 0; i < numBricks; i++)
            {
                if(bricks[i].getVisibility())
                {
                    //벽돌을 깼는지 확인
                    if (ballX + ballWidth >= bricks[i].col * bricks[i].width
                            && ballX <= bricks[i].col * bricks[i].width + bricks[i].width
                            && ballY <= bricks[i].row * bricks[i].heigth + bricks[i].heigth
                            && ballY >= bricks[i].row * bricks[i].heigth)
                    {
                        if(mpBreak != null)
                        {
                            mpBreak.start();
                        }
                        velocity.setY((velocity.getY() + 1) * -1);
                        bricks[i].setInvisible();
                        points += 15;
                        brokenBricks++;
                        if(brokenBricks == 24)
                        {
                            GameOver();
                        }
                    }
                }
            }
            if(brokenBricks == numBricks)
            {
                gameOver = true;
            }
            if(!gameOver)
            {
                handler.postDelayed(runnable, UPDATE_MILLIS);
            }

        }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 터치가 필요한 곳은 패달 뿐임.
        float touchX = event.getX();
        float touchY = event.getY();
        if(touchY >= paddleY)
        {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN)
            {
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
            if (action == MotionEvent.ACTION_MOVE)
            {
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if(newPaddleX <= 0)
                {
                    paddleX = 0;
                } else if (newPaddleX >= dWidth - paddle.getWidth()) {
                    paddleX = dWidth - paddle.getWidth();
                }else {
                    paddleX = newPaddleX;
                }
            }
        }

        return true;
    }

    private void GameOver() { // 인텐트를 사용한 Ending 클래스 호출
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, Ending.class);
        intent.putExtra("points", points); // 점수 반환
        context.startActivity(intent);
        ((Activity) context).finish();

    }

    private int xVelocity() {
        int[] values = {-35, -30, -25, 25, 30, 35};
        int index = random.nextInt(6);
        return values[index];
    }
}
