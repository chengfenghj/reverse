package com.hjchengfeng.reversi_20.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.hjchengfeng.reversi_20.Model.ImageLibrary;
import com.hjchengfeng.reversi_20.Model.ReversiBoard;


/**
 * Created by ASUS on 2016/9/7.
 */
public class ReversiView extends SurfaceView implements SurfaceHolder.Callback{

    private final int LENGHT =8;                         //棋盘宽度
    private boolean running =true;                    //线程控制
    private float scale;
    private int bLenght;           //background_lenght
    private int cLenght;           //chesses_lenght
    private int rx,ry;                       //用来暂存电脑下的坐标
    private int robotValue =10;                 //电脑的棋子颜色
    private boolean robotAct =false;       //标记是否是电脑的回合
    private boolean change =false;          //标记棋子是否在变化的过程中

    private Rect brect;         //用来画棋盘的矩形
    private Rect crect;        //用来画棋子的矩形

    private ReversiBoard board;            //实例化棋盘
    private ImageLibrary images;          //实例化图片库
    private SurfaceHolder surfaceHolder;

    public ReversiView(Context context){
        this(context, null, 0);
    }
    public ReversiView(Context context,AttributeSet attrs){
        this(context, attrs, 0);
    }
    public ReversiView(Context context,AttributeSet attrs,int style){
        super(context, attrs, style);
        scale =context.getResources().getDisplayMetrics().density;
        WindowManager wm =(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        bLenght =wm.getDefaultDisplay().getWidth()-(int)(40*scale+0.5f);
        cLenght =bLenght/8;
        brect =new Rect(0,0,bLenght,bLenght);
        surfaceHolder =getHolder();
        board =new ReversiBoard();
        init();
        images =new ImageLibrary(this.getResources());
        surfaceCreated(surfaceHolder);
    }
    //初始化
    public void init(){
        board.init();
    }
    //对点击动作的回应
    public boolean action(int x,int y,int act){
        //如果是电脑的回合，点击无效，返回false
        if(robotAct)
            return false;
        //如果棋子的图片还在变化中，点击无效，返回false
        if(change)
            return false;
        //如果点在棋盘之外，点击无效，返回false
        if(y>=8)
            return false;
        //如果游戏已经结束，点击无效，返回false
        if(board.getGameover())
            return false;
        //对下棋事件进行响应
        return board.putIn(x,y,act,true);
    }
    public void robotAction(){
        if(change)
            return;
        board.putIn(rx, ry, robotValue,false);
        robotAct =false;
    }
    public void robotAct(int x,int y){
        rx =x;
        ry =y;
        robotAct =true;
    }

    public void setRobotValue(int robotValue){
        this.robotValue =robotValue;
    }
    public void setRobotAct(boolean robotAct){
        this.robotAct =robotAct;
    }
    public boolean getRobotAct(){
        return robotAct;
    }
    public int getcLenght(){
        return cLenght;
    }
    public boolean actable(int value){
        return board.actable(value);
    }
    public void gameover(){
        board.gameover();
    }
    public boolean regret(){
        return board.regret();
    }
    public int getBlackAmount(){
        return board.getBlackAmount();
    }
    public int getWhiteAmount(){
        return board.getWhiteAmount();
    }
    public int getWinner(){
        return board.getWinner();
    }
    public boolean getGameover(){
        return board.getGameover();
    }
    //获取与棋盘上对应的图片
    private Bitmap getImage(int x,int y){
        int index =board.getValue(x, y);
        if(index == 0||index == 10)
            return images.getChesses(index);
        if(index == 19)
            board.setValue(x,y,0);
        else
            board.setValue(x,y,index+1);
        if(!change)
            change =true;
        return images.getChesses(index);
    }
    //绘画函数
    public void render(Canvas canvas){
        //画背景
        Paint paint1 =new Paint();
        canvas.drawBitmap(images.getBackground(), null, brect, paint1);
        //画线条
        Paint paint2 =new Paint();
        paint2.setColor(Color.BLACK);
        for(int i=0;i<LENGHT;i++){
                canvas.drawLine(0,0+i* cLenght,bLenght,0+i* cLenght,paint2);
                canvas.drawLine(0+i* cLenght,0,0+i* cLenght,bLenght,paint2);
        }
        //画棋子
        change =false;
        for(int i=0;i<LENGHT;i++){
            for(int j=0;j<LENGHT;j++){
                if(board.getValue(i,j) == 20)
                    continue;
                crect =new Rect(i* cLenght,j* cLenght,(i+1)*cLenght,(j+1)*cLenght);      //指定棋子在哪一个矩形中绘制
                canvas.drawBitmap(getImage(i,j), null, crect, paint1);                //绘制棋子
            }
        }
        if(robotAct)
            robotAction();
    }
    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int mWidth =MeasureSpec.getSize(widthMeasureSpec);
        int mHeight =MeasureSpec.getSize(heightMeasureSpec);
        int widthMode =MeasureSpec.getMode(widthMeasureSpec);
        int heightMode =MeasureSpec.getMode(heightMeasureSpec);

        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST)
            setMeasuredDimension(bLenght,bLenght);
        else if(widthMode == MeasureSpec.AT_MOST)
            setMeasuredDimension(bLenght,mHeight);
        else if(heightMode == MeasureSpec.AT_MOST)
            setMeasuredDimension(mWidth,bLenght);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running =true;
        new RenderThread().start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running =false;
    }
    class RenderThread extends Thread implements Runnable{

        public void run(){
            Canvas canvas =null;
            while (running){
                try{
                    sleep(80);
                    canvas =surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder){
                        render(canvas);
                    }
                }catch (Exception e){}
                finally {
                    if(canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
