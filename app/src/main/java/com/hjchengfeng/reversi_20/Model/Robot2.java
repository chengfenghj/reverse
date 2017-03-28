package com.hjchengfeng.reversi_20.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2017/1/31.
 */
public class Robot2 {

    private final int LENGHT =8;
    private final int BLACK =0;
    private final int WHITE =10;
    private final int EMPTY =20;
    private final int infinity =500;            //定义极大极小值搜索的无穷大

    private int action;               //用来记录电脑的棋子颜色
    private int[][] board;
    private int[][] mboard;
    private boolean ready =false;     //标记结果是否已经准备好
    private Coord result;
    private int searchDepth =4;         //极大极小值搜索深度
    int time =0;
    int cut =0;

    public Robot2(){
        action =WHITE;
        board =new int[LENGHT][LENGHT];
        mboard =new int[LENGHT][LENGHT];
        result =new Coord(4,4);
        init();
    }
    //初始化棋盘
    public void init(){
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
    public int getAction(){
        return action;
    }
    public void setAction(int action){
        this.action =action;
    }
    public Coord getResult(){
        if(ready) {
            ready =false;
            return result;
        }
        return null;
    }
    //传入玩家下子的位置
    public void putIn(int x,int y,int value){
        int ax,ay;
        List<Coord> coords =new ArrayList<Coord>();
        Coord coord =new Coord(0,0);
        ready =false;
        int[][] sboard =new int[LENGHT][LENGHT];
        //1、记录棋盘
        if(value == otherColor(action))
            copyBoard(mboard, board);
        //2、更新棋盘
        coords =search(x,y,value,board);
        board[x][y] = value;          //放子
        board =eat(coords, board);                         //吃子
        if(value == action)
            return;
        //3、极大极小值搜索
        copyBoard(sboard,board);
//        result =getBestMove(sboard);
        result =maxSearch(searchDepth,-infinity,infinity,coord,sboard).coord;
        ready =true;
    }
    //玩家悔棋
    public void regret(){
        copyBoard(board, mboard);
    }
    //极大值搜索
    private ABValue maxSearch(int depth,int alpha,int beta,Coord coord,int[][] sboard){
        List<Coord> actions;                     //用来保存接下来要搜索的行动力序列
        int[][] xboard =new int[LENGHT][LENGHT];
        ABValue maxValue =new ABValue(alpha,beta,coord);      //记录新的alpha和beta
        ABValue minValue;
        //如果是叶子节点
        if(depth == 0){
            maxValue.alpha =evaluate(sboard)+actionScore(coord.x, coord.y)*2;
            maxValue.score =maxValue.alpha;
//            System.out.println("max="+maxValue.alpha);
            return maxValue;
        }
        actions =findActionable(action, sboard);
        //如果是未搜索到尽头的叶子节点
        if(actions.size() == 0){
//            System.out.println("max="+maxValue.alpha);
            maxValue.alpha =evaluate(sboard)+actionScore(coord.x,coord.y)*2;
            maxValue.score =maxValue.alpha;
            return maxValue;
        }
        for(int i =0;i<actions.size();i++){
            copyBoard(xboard, sboard);
            xboard =virtualChess(actions.get(i).x,actions.get(i).y,action,xboard);
            if(depth == searchDepth)
                minValue =minSearch(depth - 1,- maxValue.beta,- maxValue.alpha, actions.get(i), xboard);
            else
                minValue =minSearch(depth - 1,-maxValue.beta,  -maxValue.alpha, coord, xboard);
            //如果返回的值（beta）小于现在的值（alpha），则进行剪枝
            if(minValue.beta <= maxValue.alpha)
                return maxValue;
            System.out.println("min="+minValue.beta);
            maxValue.alpha =minValue.beta;
            maxValue.coord = actions.get(i);
        }
//        System.out.println(maxValue.alpha + "   " + maxValue.beta);
        return maxValue;
    }
    //极小值搜索
    private ABValue minSearch(int depth,int alpha,int beta,Coord coord,int[][] sboard){
        List<Coord> actions;                     //用来保存接下来要搜索的行动力序列
        int[][] xboard =new int[LENGHT][LENGHT];
        ABValue minValue =new ABValue(alpha,beta,coord);//如果是叶子节点
        ABValue maxValue;
        if(depth == 0){
            minValue.beta =evaluate(sboard)-actionScore(coord.x,coord.y)*2;
            minValue.score =minValue.beta;
//            System.out.println("min="+minValue.beta);
            return minValue;
        }
        actions =findActionable(otherColor(action), sboard);
        //如果是未搜索到尽头的叶子节点
        if(actions.size() == 0){
            minValue.beta =evaluate(sboard)-actionScore(coord.x,coord.y)*2;
            minValue.score =minValue.beta;
//            System.out.println("min="+minValue.beta);
            return minValue;
        }
        minValue.score =infinity;
        for(int i =0;i<actions.size();i++){
            copyBoard(xboard, sboard);
            xboard = virtualChess(actions.get(i).x, actions.get(i).y, otherColor(action), xboard);
            maxValue = maxSearch(depth - 1, -minValue.beta, -minValue.alpha, coord, xboard);
            //如果返回的值（alpha）大于现在的值（beta），则进行剪枝
            if(maxValue.alpha >= minValue.beta)
                return minValue;
            System.out.println("max="+maxValue.alpha);
            minValue.beta =maxValue.alpha;
            minValue.coord = actions.get(i);
        }
//        System.out.println(minValue.alpha+"   "+minValue.beta);
        return minValue;
    }
    //计算任意局面的棋盘的评分，
    // 返回棋子数+行动力*3
    private int evaluate(int[][] sboard){
        int robotScore =0;
        int playerScore =0;
        for(int i =0;i<LENGHT;i++){
            for(int j =0;j<LENGHT;j++){
                if(sboard[i][j] == EMPTY)
                    continue;
                if(sboard[i][j] == action)
                    robotScore +=chessScore(i, j);
                if(sboard[i][j] == otherColor(action))
                    playerScore +=chessScore(i, j);
            }
        }
        robotScore +=findActionable(action,sboard).size();
        playerScore +=findActionable(otherColor(action),sboard).size();
        return robotScore -playerScore;
    }
    //搜索棋盘，看看有多少个棋子被吃掉,并记录被吃掉的棋子
    private List<Coord> search(int x,int y,int value,int[][] sboard){
        int ax =x;
        int ay =y;
        List<Coord> scoords =new ArrayList<Coord>();
        List<Coord> c =new ArrayList<Coord>();
        scoords.clear();     //清空

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
                        c.clear();
                        break;
                    }
                    //如果搜索到空格，说明这个方向没有吃子
                    if(sboard[ax][ay] == EMPTY){
                        c.clear();
                        break;
                    }
                    //如果搜索到相同颜色的棋子，说明有吃子
                    if(sboard[ax][ay] == value){
                        break;
                    }
                    //继续搜索
                    Coord coord =new Coord(ax,ay);
                    c.add(coord);
                }

                scoords.addAll(c);        //将吃掉的子记录下来
                c.clear();           //清空c
            }
        }
        return scoords;
    }
    //虚拟落子
    private int[][] virtualChess(int x,int y,int value,int[][] sboard){
        List<Coord> etas;         //暂存被吃的棋子序列
        etas =search(x,y,value,sboard);                  //获取吃子序列
        sboard =eat(etas,sboard);                               //吃子
        return sboard;
    }
    //吃子动作
    private int[][] eat(List<Coord> coords,int[][] sboard){
        int x;
        int y;
        int n =coords.size();
        for(int i=0;i<n;i++){
            x =coords.get(i).x;
            y=coords.get(i).y;
            sboard[x][y] =otherColor(sboard[x][y]);
        }
        return sboard;
    }
    //计算指定颜色棋子的行动力序列
    private List<Coord> findActionable(int value,int[][] sboard){
        List<Coord> scoords =new ArrayList<Coord>();
        for(int i =0;i < LENGHT;i++){
            for(int j =0;j < LENGHT;j++){
                if(sboard[i][j] != EMPTY)
                    continue;                      //如果已有棋子，跳过
                if(search(i, j, value, sboard).size() > 0){
                    Coord c =new Coord(i,j);
                    scoords.add(c);
                }
            }
        }
        return scoords;
    }
    //棋子位置分值表
    private int chessScore(int x, int y){
        //如果位置在角上
        if((x == 0 || x == 7)&&(y == 0 || y == 7))
            return 10;
        //如果位置在边上
        if(x == 0 || x == 7 || y == 0 || y == 7)
            return 5;
        //如果在里面
        return 1;
    }
    //行动力位置分值表
    private int actionScore(int x,int y){
        //如果位置在角上
        if((x == 0 || x == 7)&&(y == 0 || y == 7))
            return 100;
        //如果位置在角旁边的边上
        if(((x == 1 || x == 6)&&(y == 0 || y == 7))||((x == 0 || x == 7)&&(y == 1 || y == 6)))
            return -15;
        //如果位置在边上
        if(x == 0 || x == 7 || y == 0 || y == 7)
            return 8;
        //如果位置在星位上
        if((x == 1 || x == 6)&&(y == 1 || y == 6))
            return -40;
        //如果位置在第二层
        if(x == 1 || x == 6 || y == 1 || y == 6)
            return -4;
        //如果位置在里面的角上
        if((x == 2 || x == 5)&&(y == 2 || y == 5))
            return 3;
        return 1;
    }

    private int otherColor(int value){
        if(value == BLACK)
            return WHITE;
        return BLACK;
    }
    //复制棋盘
    private void copyBoard(int[][] board1,int[][] board2){
        for(int i =0;i < LENGHT;i++){
            for(int j =0;j < LENGHT;j++){
                board1[i][j] =board2[i][j];
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
}
