package com.hjchengfeng.reversi_20.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/9/8.
 */
public class ReversiBoard {

    private final int LENGHT =8;        //棋盘长度常量
    private final int BLACK =0;        //黑棋常量
    private final int WHITE =10;       //白棋常量
    private final int EMPTY =20;       //空常量

    private int blackAmount;          //记录黑棋的数量
    private int mblackAmount;        //记录上一步黑棋的数量
    private int whiteAmount;         //记录白棋的数量
    private int mwhiteAmount;         //记录上一步白棋的数量
    private boolean regretable;       //记录是否可以悔棋
    private boolean gameover;       // 记录游戏是否结束
    private int winner;              //记录赢家

    private int[][] board;           //二维数组存放棋盘信息
    private int[][] mboard;         //用来记忆棋盘的二维数组
    private List<Coord> coords;      //用来暂存被吃掉棋子的坐标

    public ReversiBoard(){
        board =new int[LENGHT][LENGHT];
        mboard =new int[LENGHT][LENGHT];
        coords =new ArrayList<Coord>();
    }
    //棋盘初始化
    public void init(){
        blackAmount =2;
        whiteAmount =2;
        mblackAmount =2;
        mwhiteAmount =2;
        gameover =false;
        regretable =false;          //游戏刚开始的时候不能悔棋
        winner =EMPTY;           //胜利者为空常量，标记没有人赢
        for(int i=0;i<LENGHT;i++){
            for(int j=0;j<LENGHT;j++){
                board[i][j]= EMPTY;
                mboard[i][j]= EMPTY;
            }
        }
        board[3][3] =BLACK;
        board[4][4] =BLACK;
        board[3][4] =WHITE;
        board[4][3] =WHITE;

        mboard[3][3] =BLACK;
        mboard[4][4] =BLACK;
        mboard[3][4] =WHITE;
        mboard[4][3] =WHITE;
    }
    //获取棋盘上指定位置棋子的状态——无棋子、黑棋、白棋或者变化中
    public int getValue(int x,int y){
        return board[x][y];
    }
    //设置棋盘上指定位置棋子的状态
    public void setValue(int x,int y,int value){
        board[x][y] = value;
    }
    //获取黑子的数量
    public int getBlackAmount(){
        return blackAmount;
    }
    //获取白子的数量
    public int getWhiteAmount(){
        return whiteAmount;
    }
    public int getWinner(){
        return winner;
    }
    public boolean getGameover(){
        return gameover;
    }
    //指定位置放入棋子
    public boolean putIn(int x,int y,int value,boolean player){
        int eat;
        //如果指定位置有棋子
        if(!isEmpty(x,y))
            return false;
        //如果一个子都没吃掉
        eat =search(x,y,value);
        if(eat == 0)
            return false;
        if(player) {
            copyBoard(0);                 //先记录棋盘
            mblackAmount = blackAmount;
            mwhiteAmount = whiteAmount;
        }
        setValue(x, y, value);          //放子
        eat();                         //吃子
        numberChange(eat, value);      //改变棋子数量
        regretable=true;             //有下子就可以悔棋
        check();                      //检查游戏是否结束
        return true;
    }
    //搜索棋盘，看看有多少个棋子被吃掉
    private int search(int x,int y,int value){
        int ax =x;
        int ay =y;
        int eat =0;        //吃掉的棋子数
        int k =0;         //
        List<Coord> c =new ArrayList<Coord>();
        coords.clear();     //清空

        //八个方向搜索
        for(int i= -1;i<=1;i++){
            for(int j= -1;j<=1;j++){
                if(i==0&&j==0)
                    continue;
                //重置ax、ay
                ax =x;
                ay =y;
                //搜索
                while (true){       //下一个搜索的位置在棋盘上并且有棋子
                    ax +=i;
                    ay +=j;
                    //如果搜索到棋盘之外，说明这个方向没有吃子
                    if(isOutBoard(ax, ay)){
                        k=0;
                        c.clear();
                        break;
                    }
                    //如果搜索到空格，说明这个方向没有吃子
                    if(isEmpty(ax,ay)){
                        k=0;
                        c.clear();
                        break;
                    }
                    //如果搜索到相同颜色的棋子，说明有吃子
                    if(isSameChess(ax,ay,value)){
                        break;
                    }
                    //继续搜索
                    k++;
                    Coord coord =new Coord(ax,ay);
                    c.add(coord);
                }
                //如果没有吃子，进行下一个方向的搜索
                if(k == 0)
                    continue;
                
                coords.addAll(c);        //将吃掉的子记录下来
                eat +=k;               //记录吃掉的子数
                k =0;                 //重置k
                c.clear();           //清空c
            }
        }
        return eat;
    }
    //吃子动作
    private void eat(){
        int x;
        int y;
        int n =coords.size();
        for(int i=0;i<n;i++){
            x =coords.get(i).x;
            y=coords.get(i).y;
            board[x][y]++;
        }
    }
    //及时改变棋盘上统计棋子数目的值
    private void numberChange(int amount,int value){
        if(value == BLACK){
            blackAmount +=amount+1;
            whiteAmount -=amount;
            return;
        }
        blackAmount -=amount;
        whiteAmount +=amount+1;
    }
    //用来检查指定棋手是否有行动力
    public boolean actable(int value){
        int eat;
        for(int i =0;i < LENGHT;i++){
            for( int j =0;j < LENGHT;j++){
                if(board[i][j] == EMPTY) {                 //判断搜索到的位置是否没有棋子，没有棋子才尝试放入
                    eat =search(i, j, value);                 //看看这个位置是否有吃子
                    if(eat > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //游戏结束的时候判断是谁赢了
    public void gameover(){
        gameover =true;
        if(blackAmount > whiteAmount)
            winner =BLACK;
        else
            winner =WHITE;

    }
    //用来检查游戏是否结束
    private void check(){
        //第一种情况，棋盘被下满
        if(blackAmount+whiteAmount == 64)
            gameover();
        //第二种情况，有一方棋子被吃完
        if(blackAmount == 0||whiteAmount == 0)
            gameover();
        //第三种情况，双方都无子可下
        //单独判断
    }
    public boolean regret(){
        if(regretable) {
            copyBoard(1);
            blackAmount = mblackAmount;
            whiteAmount = mwhiteAmount;
            regretable =false;
            return true;
        }
        return false;
    }
    //复制棋盘，用来记忆棋盘，或者是悔棋
    private void copyBoard(int dire){
        if(dire == 0) {
            for (int i = 0; i < LENGHT; i++) {
                for (int j = 0; j < LENGHT; j++) {
                    mboard[i][j] = board[i][j];
                }
            }
        }
        else {
            for (int i = 0; i < LENGHT; i++) {
                for (int j = 0; j < LENGHT; j++) {
                    board[i][j] = mboard[i][j];
                }
            }
        }
    }
    //判断指定位置是否在棋盘之外
    private boolean isOutBoard(int x,int y){
        if(x < 0 || y < 0)
            return true;
        if(x >= LENGHT || y >= LENGHT)
            return true;
        return false;
    }
    //判断是否是相同颜色的棋子
    private boolean isSameChess(int x,int y,int value){
        if(getValue(x,y) == value)
            return true;
        return false;
    }
    //指定位置是否没有棋子
    public boolean isEmpty(int x,int y){
        if(board[x][y]==EMPTY)
            return true;
        return false;
    }
}
