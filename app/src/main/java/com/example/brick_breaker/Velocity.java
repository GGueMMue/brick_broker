package com.example.brick_breaker;

public class Velocity {
//공 속도 제어 + 엑세스 용(x, y)
    private int x, y;

    public Velocity(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX() //getter
    {
        return x;
    }
    public void setX(int x) //setter
    {
        this.x = x;
    }
    public int getY()
    {
        return y;
    }
    public void setY(int y)
    {
        this.y = y;
    }
}
