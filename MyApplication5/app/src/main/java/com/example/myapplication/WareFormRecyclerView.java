package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WareFormRecyclerView extends RecyclerView {

    public int paddingleft = 0;                    // 刻度的起始坐标(像素)
    public float density = 1;              // 手机屏幕密度
    public float secondPreDp = 0.05f;      // 一个dp多少秒

    public int drawcolor = 0xff333333;     // 刻度颜色
    public int fontSize = 36;              // 刻度字体大小
    public int shortTerm = 30;             // 1秒钟刻度线长度(像素)
    public int middleTerm = 40;            // 5秒钟刻度线长度(像素)
    public int longTerm  = 50;             // 10秒钟刻度线长度(像素)

    private LinearLayoutManager layoutManager;

    private SimpleDateFormat formatter;
    private Date date;

    private Paint paint;
    private TextPaint textPaint;
    private Rect rect;


    public WareFormRecyclerView(@NonNull Context context) {
        super(context);
    }

    public WareFormRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setHasFixedSize(true);


        // 设置横向布局
        layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        setLayoutManager(layoutManager);

        // 初始化变量
        paint = new Paint();
        paint.setColor(drawcolor);
        paint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setColor(drawcolor);
        textPaint.setTextSize(fontSize);

        rect = new Rect();
        formatter = new SimpleDateFormat("mm:ss", Locale.CHINA);
        date = new Date();

    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        // 显示刻度
        int scrollOffsetX= computeHorizontalScrollOffset();


        // 用来计算刻度开始的时间
        int startx = Math.max(scrollOffsetX - paddingleft,0);

        // 刻度开始画的起始坐标
        int paddingx = Math.max(paddingleft - scrollOffsetX,0);

        for (int i = (int) Math.max(startx / density - 20,0); i <= ( startx + getMeasuredWidth()) / density; i++){

            int x = (int) (i * density - startx + paddingx);

            // 10秒位置
            if ((secondPreDp*100*i) % 1000 == 0){
                rect.set(x,getMeasuredHeight()-longTerm, (int) (x+ density),getMeasuredHeight());
                c.drawRect(rect,paint);

                date.setTime((long) (secondPreDp * i * 1000L));
                String dateString = formatter.format(date);

                // 显示时间
                textPaint.getTextBounds(dateString, 0, dateString.length(), rect);
                c.drawText(dateString + "", x-rect.width()/2, getMeasuredHeight()-70, textPaint);

            }

            // 5秒位置
            else if ((secondPreDp*100*i) % 500 == 0){
                rect.set(x,getMeasuredHeight()-middleTerm, (int) (x+ density),getMeasuredHeight());
                c.drawRect(rect,paint);
            }

            // 1秒位置
            else if ((secondPreDp*100*i) % 100 == 0){
                rect.set(x,getMeasuredHeight()-shortTerm, (int) (x+ density),getMeasuredHeight());
                c.drawRect(rect,paint);
            }
        }
    }

}
