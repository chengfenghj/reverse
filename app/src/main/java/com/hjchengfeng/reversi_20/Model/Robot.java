package com.hjchengfeng.reversi_20.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2017/1/15.
 */
public class Robot {

    private final int LENGHT =8;
    private final int BLACK =0;
    private final int WHITE =10;
    private final int EMPTY =20;

    private int maxScore;
    private int action;               //用来记录电脑的棋子颜色
    private int[][] board;
    private int[][] mboard;
    private boolean v =false;
    private List<Coord> result;

    public Robot(){
        action =WHITE;
        board =new int[LENGHT][LENGHT];
        mboard =new int[LENGHT][LENGHT];
        result =new ArrayList<Coord>();
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
        if(v) {
            int index =(int)(Math.random()*result.size());
            return result.get(index);
        }
        return null;
    }
    //传入玩家下子的位置
    public void putIn(int x,int y,int value){
        int ax,ay;
        List<Coord> coords =new ArrayList<Coord>();
        List<Coord> actions;            //保存行动力序列
        v =false;
        int[][] sboard =new int[LENGHT][LENGHT];
        //1、记录棋盘
        if(value == player())
            copyBoard(mboard, board);
        //2、更新棋盘
        coords =search(x,y,value,board);
        board[x][y] = value;          //放子
        eat(coords, board);                         //吃子
        if(value == action)
            return;
        //3、获取行动力序列
        actions =findActionable(action,board);
        //4、虚拟落子
        //5、计算分值
        for(int i =0;i<actions.size();i++){
            ax =actions.get(i).x;
            ay =actions.get(i).y;
            copyBoard(sboard,board);              //复制棋盘
            virtualChess(ax,ay,action,sboard);       //虚拟落子
            actions.get(i).score =valueScore(sboard);          //计算分值
        }
        //6、选取最佳落点
        result.clear();
        maxScore =-1000;
        for(int i =0;i<actions.size();i++){
            if(actions.get(i).score >= maxScore) {
                result.add(actions.get(i));
                maxScore =actions.get(i).score;
            }
        }
        v =true;
    }
    //玩家悔棋
    public void regret(){
        copyBoard(board, mboard);
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
    //搜索虚拟棋盘，看看指定位置如果下棋的话会有多少个棋子被吃掉
    private int search1(int x,int y,int value,int[][] sboard){
        int ax =x;
        int ay =y;
        int eat =0;        //吃掉的棋子数
        int k =0;         //记录每一条方向上吃掉的棋子数

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
                        break;
                    }
                    //如果搜索到空格，说明这个方向没有吃子
                    if(sboard[ax][ay] == EMPTY){
                        k=0;
                        break;
                    }
                    //如果搜索到相同颜色的棋子，说明有吃子
                    if(sboard[ax][ay] == value){
                        break;
                    }
                    //继续搜索
                    k++;
                }
                //如果没有吃子，进行下一个方向的搜索
                if(k == 0)
                    continue;

                eat +=k;               //记录吃掉的子数
                k =0;                 //重置k
            }
        }
        return eat;
    }
    //虚拟落子
    private void virtualChess(int x,int y,int value,int[][] sboard){
        List<Coord> etas =new ArrayList<Coord>();         //暂存被吃的棋子序列
        etas =search(x,y,value,sboard);                  //获取吃子序列
        eat(etas,sboard);                               //吃子
    }
    //吃子动作
    private void eat(List<Coord> coords,int[][] sboard){
        int x;
        int y;
        int n =coords.size();
        for(int i=0;i<n;i++){
            x =coords.get(i).x;
            y=coords.get(i).y;
            sboard[x][y] =otherColor(sboard[x][y]);
        }
    }
    //对于任意棋盘局面评分
    private int valueScore(int[][] sboard){
        int mscore =0;
        int robotamount =0;           //电脑棋子数量
        int playeramount =0;          //玩家棋子数量
        int robotzanding =0;         //电脑暂定子的数量
        int playerzanding =0;        //玩家暂定子的数量
        int robotaction =0;          //电脑行动力
        int playeraction =0;         //玩家行动力
        int robotwending =0;         //电脑稳定子的数量
        int playerwending =0;        //玩家稳定子的数量
        List<Coord> robotCoords =new ArrayList<Coord>();
        List<Coord> playerCoords =new ArrayList<Coord>();

        //      1、计算各自棋子数量
        robotamount =findChessAmount(action, sboard);
        playeramount =findChessAmount(player(), sboard);
        //      2、计算行动力
        robotCoords =findActionable(action, sboard);
        robotaction =robotCoords.size();
        playerCoords =findActionable(action, sboard);
        playeraction =playerCoords.size();
        //      3、计算暂定子数量
        robotzanding =temporary(action, robotamount, robotCoords, sboard);
        playerzanding =temporary(player(), playeramount, playerCoords, sboard);
        //      4、计算稳定子数量
        robotwending =fixation(action, sboard);
        playerwending =fixation(player(), sboard);

        mscore =robotamount + robotzanding + 3*robotaction + 3*robotwending -
                playeramount - playerzanding - 3*playeraction - 3*playerwending;
        return  mscore;
    }
    //计算指定颜色棋子的行动力序列
    private List<Coord> findActionable(int value,int[][] sboard){
        List<Coord> scoords =new ArrayList<Coord>();
        for(int i =0;i < LENGHT;i++){
            for(int j =0;j < LENGHT;j++){
                if(sboard[i][j] != EMPTY)
                    continue;                      //如果已有棋子，跳过
                if(search1(i, j, value, sboard) > 0){
                    Coord c =new Coord(i,j);
                    scoords.add(c);
                }
            }
        }
        return scoords;
    }
    //计算指定颜色棋子的数量
    private int findChessAmount(int value,int[][] sboard){
        int amount =0;
        for(int i =0;i < LENGHT;i++){
            for(int j =0;j < LENGHT;j++){
                if(sboard[i][j] == value)
                    amount++;
            }
        }
        return amount;
    }
    //计算指定颜色的暂定子的数量
    private int temporary(int value,int amount,List<Coord> scoords,int[][] sboard){
        int num =0;
        boolean b =true;          //标记棋子是否被记录过
        List<Coord> c;              //一步棋的吃子序列
        List<Coord> tcoords =new ArrayList<Coord>();        //所有被吃掉的棋子序列
        for(int i =0;i<scoords.size();i++){                   //依次放入棋子
            c =search(scoords.get(i).x,scoords.get(i).y,value,sboard);
            //计算吃子序列
            for(int j =0;j<c.size();j++){
                for(int k =0;k<tcoords.size();k++){
                    if(tcoords.get(k).equal(c.get(j))){
                        b =false;
                        break;
                    }
                }
                //如果这个棋子没有被记录，记下来
                if(b)
                    tcoords.add(c.get(j));
                b =true;
            }
        }
        num =amount - tcoords.size();
        return num;
    }
    //计算固定子的数量
    private int fixation(int value,int[][] sboard){
        int num =0;
        int i;
        int dx =1;        //x方向上搜索的格子数
        int dy =1;        //y方向上搜索的格子数
        int ax =0;        //x方向的搜索坐标
        int ay =0;        //y方向的搜索坐标
        boolean m =false;      //搜索是否结束
        if(sboard[ax][ay] == value){
            num++;
            m =true;
        }
        while (m){
            m =false;
            if(isOutBoard(ax,ay))
                break;
            //搜索x方向
            for(i =0;i<dx;i++){
                if(sboard[i][ay] == value)
                    num++;
                else
                    break;
            }
            if(dx == ax)
                dx++;
            else
                dx =i;
            //搜索y方向
            for(i =0;i<dy;i++){
                if(sboard[ax][i] == value)
                    num++;
                else
                    break;
            }
            if(dy == ay)
                dy++;
            else
                dy =i;
            //如果顶点也是，数量减一
            if(sboard[ax][ay] == value)
                num--;
            //下一个
            ax++;
            ay++;
            if(isOutBoard(ax,ay))
                break;
            //如果下一颗棋子还是
            if(sboard[0][ay] == value||sboard[ax][0] == value)
                m =true;
        }
        ax =7;
        ay =0;
        dx =1;
        dy =1;
        if(sboard[ax][ay] == value){
            num++;
            m =true;
        }
        while (m){
            m =false;
            if(isOutBoard(ax,ay))
                break;
            //搜索x方向
            for(i =0;i<dx;i++){
                if(sboard[7-i][ay] == value)
                    num++;
                else
                    break;
            }
            if(dx == (8-ax))
                dx++;
            else
                dx =i;
            //搜索y方向
            for(i =0;i<dy;i++){
                if(sboard[ax][i] == value)
                    num++;
                else
                    break;
            }
            if(dy == (8-ay))
                dy++;
            else
                dy =i;
            //如果顶点也是，数量减一
            if(sboard[ax][ay] == value)
                num--;
            //下一个
            ax--;
            ay++;
            if(isOutBoard(ax,ay))
                break;
            //如果下一颗棋子还是
            if(sboard[7][ay] == value||sboard[ax][0] == value)
                m =true;
        }
        ax =7;
        ay =7;
        dx =1;
        dy =1;
        if(sboard[ax][ay] == value){
            num++;
            m =true;
        }
        while (m){
            m =false;
            if(isOutBoard(ax,ay))
                break;
            //搜索x方向
            for(i =0;i<dx;i++){
                if(sboard[7-i][ay] == value)
                    num++;
                else
                    break;
            }
            if(dx == (8-ax))
                dx++;
            else
                dx =i;
            //搜索y方向
            for(i =0;i<dy;i++){
                if(sboard[ax][7-i] == value)
                    num++;
                else
                    break;
            }
            if(dy == (8-ay))
                dy++;
            else
                dy =i;
            //如果顶点也是，数量减一
            if(sboard[ax][ay] == value)
                num--;
            //下一个
            ax--;
            ay--;
            if(isOutBoard(ax,ay))
                break;
            //如果下一颗棋子还是
            if(sboard[7][ay] == value||sboard[ax][7] == value)
                m =true;
        }
        ax =0;
        ay =7;
        dx =1;
        dy =1;
        if(sboard[ax][ay] == value){
            num++;
            m =true;
        }
        while (m){
            m =false;
            if(isOutBoard(ax,ay))
                break;
            //搜索x方向
            for(i =0;i<dx;i++){
                if(sboard[i][ay] == value)
                    num++;
                else
                    break;
            }
            if(dx == ax)
                dx++;
            else
                dx =i;
            //搜索y方向
            for(i =0;i<dy;i++){
                if(sboard[ax][7-i] == value)
                    num++;
                else
                    break;
            }
            if(dy == (8-ay))
                dy++;
            else
                dy =i;
            //如果顶点也是，数量减一
            if(sboard[ax][ay] == value)
                num--;
            //下一个
            ax++;
            ay--;
            if(isOutBoard(ax,ay))
                break;
            //如果下一颗棋子还是
            if(sboard[7][ay] == value||sboard[ax][7] == value)
                m =true;
        }
        return num;
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
    //获取玩家的棋子颜色
    private int player(){
        if(action == BLACK)
            return WHITE;
        return BLACK;
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
