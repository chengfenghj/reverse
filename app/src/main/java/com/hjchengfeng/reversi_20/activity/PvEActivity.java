package com.hjchengfeng.reversi_20.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjchengfeng.reversi_20.Model.Coord;
import com.hjchengfeng.reversi_20.Model.Robot2;
import com.hjchengfeng.reversi_20.R;
import com.hjchengfeng.reversi_20.view.ReversiView;

/**
 * Created by ASUS on 2017/1/11.
 */
public class PvEActivity extends Activity{

    private Robot2 robot;
    private ReversiView reversiView;
    private ImageView actor;
    private ImageView arrow;
    private TextView black;
    private TextView blackText;
    private TextView white;
    private TextView whiteText;
    private Button newgame;
    private Button regret;
    private Button comeback;
    private int action =0;             //记录玩家的棋子颜色，初始黑棋
    private int acting =0;            //记录正在下的棋子的颜色，初始黑棋

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_game1);
        robot =new Robot2();
        reversiView =(ReversiView)findViewById(R.id.reversiView);
        actor =(ImageView)findViewById(R.id.actor);
        arrow =(ImageView)findViewById(R.id.arrow);
        black =(TextView)findViewById(R.id.black_amount);
        blackText =(TextView)findViewById(R.id.black_text);
        white =(TextView)findViewById(R.id.white_amount);
        whiteText =(TextView)findViewById(R.id.white_text);
        newgame =(Button)findViewById(R.id.newgame);
        regret =(Button)findViewById(R.id.regret);
        comeback =(Button)findViewById(R.id.comeback);

        blackText.setText("玩家");
        whiteText.setText("电脑");

        //新局按钮被按下
        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reversiView.init();     //棋盘重新初始化
                robot.init();
                acting =0;        //行动者换为黑棋
            }
        });
        //返回按钮被按下
        comeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PvEActivity.this.finish();
            }
        });
        regret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reversiView.regret();
                robot.regret();
                setText();
            }
        });
        reversiView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //得到点击坐标
                int x =(int)event.getX()/reversiView.getcLenght();
                int y =(int)event.getY()/reversiView.getcLenght();
                boolean eff =reversiView.action(x,y, action);        //判断点击是否有效
                //有效
                if(eff){
                    setText();
                    if(reversiView.getGameover()) {
                        gameover();
                        return true;
                    }
                    actChange();
                    robot.putIn(x, y, action);
                    if(reversiView.actable(robot.getAction()) == false) {         //如果电脑无子可下
                        disactable();
                        actChange();
                        if(reversiView.actable(action) == false) {    //如果玩家也无子可下
                            reversiView.gameover();                  //游戏结束
                            gameover();
                        }
                        return true;
                    }
                    robotAct();
                    //因为电脑下子之后不能直接改变最终棋局的棋子数，
                    // 所以如果最后一步是电脑下，棋子总数为63则说明棋盘被下满
                    if(reversiView.getBlackAmount()+reversiView.getWhiteAmount() == 63) {
                        gameover();
                        return true;
                    }
                    return true;
                }
                //无效
                else
                    return false;
            }
        });
    }
    private void robotAct(){
        Coord coord;
        while(true) {
            coord= robot.getResult();
            if(coord != null)
                break;
        }
        reversiView.robotAct(coord.x, coord.y);
        robot.putIn(coord.x, coord.y, robot.getAction());
        actChange();
//        System.out.println("电脑下子："+coord.x+" "+coord.y);
    }
    //改变act的值，轮到另一个人行动
    private void actChange(){
        if(acting ==0)
            acting =10;
        else
            acting =0;
        imageChange();
    }
    //当存在有一方无子可下的时候
    private void disactable(){
        if(acting == 0)
            Toast.makeText(PvEActivity.this, "黑棋无子可下！", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(PvEActivity.this,"白棋无子可下！",Toast.LENGTH_SHORT).show();

    }
    //换一张图片，应该与行动对应
    private void imageChange(){
        //轮到黑棋行动
        if(acting == 0){
            arrow.setImageResource(R.drawable.zuojiantou);
            actor.setImageResource(R.drawable.black);
        }
        //轮到白棋行动
        else{
            arrow.setImageResource(R.drawable.youjiantou);
            actor.setImageResource(R.drawable.white);

        }
    }
    //设置棋子数目
    private void setText(){
        black.setText(getAmountText(reversiView.getBlackAmount()));
        white.setText(getAmountText(reversiView.getWhiteAmount()));
    }
    //用来将数字换成字符
    private String getAmountText(int amount){
        String text ="";
        text ="×"+String.valueOf(amount);
        return text;
    }
    private void gameover(){
        if(reversiView.getWinner() == action)
            Toast.makeText(PvEActivity.this,"你赢了！",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(PvEActivity.this,"电脑赢了！",Toast.LENGTH_SHORT).show();
    }
}
