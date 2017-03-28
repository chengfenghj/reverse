package com.hjchengfeng.reversi_20.Model;

/**
 * Created by ASUS on 2017/1/31.
 */
public class ABValue {

    public int alpha;
    public int beta;
    public Coord coord;
    public int score;

    public ABValue(){
        coord =new Coord(0,0);
    }


    public ABValue(int alpha,int beta,Coord coord){
        this.alpha =alpha;
        this.beta =beta;
        this.coord = coord;
        score =-500;
    }
}
