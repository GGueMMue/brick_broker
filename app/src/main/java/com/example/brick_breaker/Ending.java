package com.example.brick_breaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class Ending extends AppCompatActivity {
    private static final String FILE_NAME = "score.txt";
    private Context context;

    TextView tPoints; // 현재 점수 표시
    ImageView hightest; // 최고 점수 이미지뷰

    TextView scoreView; // 상위 3개 점수를 표시할 TextView

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

    public void restart(View view) {
        // 리스타트 버튼을 눌렀을 때, 재시작
        Intent intent = new Intent(Ending.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }

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
}
