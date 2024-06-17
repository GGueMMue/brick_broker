벽돌 깨기
---------------

하단의 패달을 이용하여 볼을 쳐내 벽돌을 하나하나 부숴 점수를 올리는 고전 게임 구현 코드입니다.


사용 언어 - 자바


--------------


인 게임 화면 예시들
-

![제목 없음](https://github.com/GGueMMue/brick_broker/assets/86968819/44e51851-e00b-4df9-9738-426255b21295)

시작 화면, 인 게임 화면, 결과창, 최고 점수 도달 결과창 예시


-------------

게임 진행 방법
-

어플리케이션을 켰을 때, 실행 버튼을 클릭합니다.

인 게임 내 체력은 총 4개로, 각기 초록, 노랑, 빨강, 없음으로 나눠져 있습니다.

플레이어는 공을 패달로 쳐내 모든 벽돌을 깨내야 합니다.

만일, 체력이 전부 소진 되거나 모든 벽돌을 깨는데 성공한다면 플레이어는, 최종 점수가 기록되어 종료 됩니다.

이때, 자신이 기록한 점수가 이전의 최고 기록보다 높다면 최고 점수 도달을 의미하는 이미지뷰가 마지막 화면에 뜨게 됩니다.


------------

기능 구현
-

|클래스|기능|레이아웃|
|------|---|---|
|MainActivity|시작화면|activity_main.xml|
|Brick|벽돌의 가시성 제어 및 생성| - |
|Velocity|공의 속도 제어 + 공의 getter와 setter| - |
|GameView|게임이 진행되는 클래스| - |
|Ending|게임 종료 후 점수 표기|gameover.xml|



시작화면
-

게임 시작 메소드 - startGame

    public void startGame(View view){
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

어플리케이션을 처음 실행 했을 때, 시작 버튼을 누르면, onClick으로 GameView 클래스와 연동된 이미지 버튼이 GameView 클래스를 실행시킵니다.

-

인 게임
-


**createBricks** - 벽돌 생성 메소드

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

이후, onDraw에서 그려주기 전에 벽돌 24개를 메소드를 통해 선언합니다.



**onDraw** - 공과 패달, 등등을 그려주고 움직임을 보여주는 메소드


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

GameView에서 onDraw를 통해 공의 움직임과 벽돌을 그리며,
이때, 벽돌을 깼는지, 또는 공을 놓쳤는지 여부에 대해서도 확인하게 됩니다. 벽돌을 깨는 조건은 "볼과 벽돌이 겹쳤는가?"입니다.
따라서, 이 둘이 겹쳤는지 판별하는 근거는 공과 벽돌의 사이즈에 기반됩니다.

또한 벽돌을 깼을 때의 사운드와 공을 놓쳤을 때 사운드, 공을 패달로 쳐냈을 때 사운드가 구현되어 있으며, 이는 MediaPlayer로 구현하였습니다.

게임을 계속 진행 하다 보면, 공의 속도가 계속해서 증가하게 되는데, 이를 +-35로 제한을 걸어 제어하였습니다.


**onTouchEvent**

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
패달의 움직임은 onTouchEvent로 구현하여 유동적인 움직임을 구현하였습니다.

**GameOver** - 게임 종료 메소드

    private void GameOver() { // 인텐트를 사용한 Ending 클래스 호출
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, Ending.class);
        intent.putExtra("points", points); // 점수 반환
        context.startActivity(intent);
        ((Activity) context).finish();

    }

게임이 종료 되었을 때 GameView 클래스와 Ending을 인텐트로 엮어 주었습니다. 이때, 최종 점수가 putExtra()를 통해 Ending 클래스로 넘어가게 됩니다.

-

게임 종료 화면
-

**onCreate** - 인텐트를 받아 최종 점수를 받고 메소드 2개를 실행.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gameover);
        hightest = findViewById(R.id.high_score);
        tPoints = findViewById(R.id.nowPoints);
        scoreView = findViewById(R.id.score_list);

        int points = getIntent().getExtras().getInt("points");
        tPoints.setText(String.valueOf(points));

        saveScore(points); // 점수를 저장합니다.
        displayTopScores(points); // 상위 3개 점수를 표시합니다.
    }

**restart** - 시작화면으로 돌아가는 인텐트 실행

    public void restart(View view) {
        // 리스타트 버튼을 눌렀을 때, 재시작
        Intent intent = new Intent(Ending.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

**exit** - 종료 메소드

        public void exit(View view) {
        finish();
    }


**saveScore** - 점수 저장 메소드

    private void saveScore(int points) {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND)) {
            String scoreString = points + "\n";
            fos.write(scoreString.getBytes());
            fos.flush();
            Log.d("Ending", "Score saved: " + scoreString); // 디버그 로그 추가
        } catch (Exception e) {
            Log.e("Ending", "Error saving score", e); // 에러 로그 추가
            e.printStackTrace();
        }
    }
인텐트를 통해 반환된 현재 점수를 파일 입출력을 통해 points + "\n" 방식으로 저장합니다. 
코딩 중, 문제를 확인하기 위해 로그캣 로그 함수를 추가하여 작성하였습니다.

**displayTopScore** - 최고 점수 표출 및 3위까지의 점수 순위 표현

    private void displayTopScores(int currentPoints) {
        ArrayList<Integer> scores = new ArrayList<>();

        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    scores.add(Integer.parseInt(line.trim())); // trim()으로 공백 제거 후 파싱
                    Log.d("Ending", "Read score: " + line.trim()); // 디버그 로그 추가
                } catch (NumberFormatException e) {
                    Log.e("Ending", "Invalid number format in file: " + line, e); // 잘못된 형식 처리
                }
            }

            Collections.sort(scores, Collections.reverseOrder());

            if (!scores.isEmpty() && currentPoints >= scores.get(0)) {
                hightest.setVisibility(View.VISIBLE);  // 현재 점수가 기존 1등보다 높은지 확인 후 이미지 뷰를 표시
                Log.d("Ending", "New high score achieved: " + currentPoints);
            }

            StringBuilder topScoresText = new StringBuilder();
            for (int i = 0; i < Math.min(3, scores.size()); i++) {
                topScoresText.append(scores.get(i)).append("\n");
                Log.d("Ending", "Top score: " + scores.get(i));
            }

            scoreView.setText(topScoresText.toString());

        } catch (Exception e) {
            Log.e("Ending", "Error reading scores", e);
            e.printStackTrace();
        }
    }

순위 체크를 위해 Integer로 선언된 scores 배열에 파일이 각 줄을 읽어옵니다. 이때, 각 줄을 저장할 때 trim으로 공백을 제거하여 파싱합니다.
그 후, scores에 저장된 각 정보를 내림차순으로 정렬하고, 현재 점수와 scores의 0번째 인덱스와 비교하여 누가 더 점수가 큰지 비교합니다.
이때, 현재 점수가 scores[0]과 동일하거나 이보다 더 크다면, 최고점 달성 이미지뷰를 Visible로 변경합니다.

그 다음, 상위 3개의 점수를 StringBuilder에 추가하고, 현 점수 아래에 표출되는 scoreView의 텍스트를 StringBuilder로 받아온 텍스트로 설정합니다.


