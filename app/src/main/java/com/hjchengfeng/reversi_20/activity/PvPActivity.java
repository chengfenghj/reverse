package com.hjchengfeng.reversi_20.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjchengfeng.reversi_20.R;
import com.hjchengfeng.reversi_20.view.ReversiView;

/**
 * Created by ASUS on 2016/9/5.
 */
public class PvPActivity extends Activity{

    private ReversiView reversiView;
    private ImageView actor;
    private ImageView arrow;
    private TextView black;
    private TextView white;
    private Button newgame;
    private Button regret;
    private Button comeback;
    private int act =0;             //记录当前行动的是黑棋还是白棋，初始黑棋

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_game1);
        reversiView =(ReversiView)findViewById(R.id.reversiView);
        actor =(ImageView)findViewById(R.id.actor);
        arrow =(ImageView)findViewById(R.id.arrow);
        black =(TextView)findViewById(R.id.black_amount);
        white =(TextView)findViewById(R.id.white_amount);
        newgame =(Button)findViewById(R.id.newgame);
        regret =(Button)findViewById(R.id.regret);
        comeback =(Button)findViewById(R.id.comeback);

        //新局按钮被按下
        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reversiView.init();     //棋盘重新初始化
                act =0;        //行动者换为黑棋
            }
        });
        //返回按钮被按下
        comeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(PvPActivity.this,MainActivity.class);
//                startActivity(intent);
                PvPActivity.this.finish();
            }
        });
        regret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reversiView.regret()) {
                    actChange();
                    setText();
                }
            }
        });
        reversiView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //得到点击坐标
                int x =(int)event.getX()/reversiView.getcLenght();
                int y =(int)event.getY()/reversiView.getcLenght();
                boolean eff =reversiView.action(x,y,act);        //判断点击是否有效
                //有效
                if(eff){
                    setText();
                    if(reversiView.getGameover()) {
                        gameover();
                        return true;
                    }
                    actChange();             //轮到另一个人行动了
                    if(reversiView.actable(act) == false) {         //如果下一个棋手无子可下
                        disactable();
                        actChange();
                        if(reversiView.actable(act) == false) {    //如果两个人都无子可下
                            reversiView.gameover();                  //游戏结束
                            gameover();
                            return true;
                        }
                    }
                    return true;
                }
                //无效
                else
                    return false;
            }
        });
    }
    //改变act的值，轮到另一个人行动
    private void actChange(){
        if(act==0)
            act =10;
        else
            act =0;
        imageChange();
    }
    //当存在有一方无子可下的时候
    private void disactable(){
        if(act == 0)
            Toast.makeText(PvPActivity.this,"黑棋无子可下！",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(PvPActivity.this,"白棋无子可下！",Toast.LENGTH_SHORT).show();

    }
    //换一张图片，应该与行动对应
    private void imageChange(){
        //轮到黑棋行动
        if(act == 0){
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
        if(reversiView.getWinner() == 0)
            Toast.makeText(PvPActivity.this,"黑棋赢了！",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(PvPActivity.this,"白棋赢了！",Toast.LENGTH_SHORT).show();
    }
}
