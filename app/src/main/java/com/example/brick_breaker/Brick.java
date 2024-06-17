package com.example.brick_breaker;

public class Brick {
// 벽돌의 가시성 및 생성
    private boolean isVisible;
    public int row, col, width, heigth;
    public Brick(int row, int col, int width, int heigth)
    {
        isVisible = true;
        this.row = row;
        this.col = col;
        this.width = width;
        this.heigth = heigth;
    }

    public void setInvisible()
    {
        isVisible = false;
    }

    public boolean getVisibility()
    {
        return isVisible;
    }

}
