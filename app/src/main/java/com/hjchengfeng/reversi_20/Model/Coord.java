package com.hjchengfeng.reversi_20.Model;

/**
 * Created by ASUS on 2016/9/8.
 */
public class Coord {

    public int x;
    public int y;
    public int score;

    public Coord(int x,int y){
        this.x =x;
        this.y =y;
        score =0;
    }
    public boolean equal(Coord coord){
        if(coord.x == this.x && coord.y == this.y)
            return true;
        return false;
    }
    public void copy(Coord coord){
        this.x =coord.x;
        this.y =coord.y;
        this.score =coord.score;
    }
}
