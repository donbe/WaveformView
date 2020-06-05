package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WareFormRecyclerView extends RecyclerView {

    private final WareFormAdapter mAdapter;
    private int paddingleft = 0;                    // 刻度的起始坐标(像素)
    public float secondPreDp = 0.05f;      // 一个dp多少秒
    public int drawcolor = 0xff3D4057;     // 刻度颜色
    public int linecolor = 0xff779FD7;     // 波形图线的颜色
    public int topLinecolor = 0xff63647C;     // 顶部和底部线颜色
    public int centLinecolor = 0xffffffff;     // 中线
    public int fontSize = 30;              // 刻度字体大小
    public int shortTerm = 15;             // 1秒钟刻度线长度(像素)
    public int middleTerm = 22;            // 5秒钟刻度线长度(像素)
    public int longTerm  = 30;             // 10秒钟刻度线长度(像素)
    private float density = 1;             // 手机屏幕密度
    private int radius = 10;
    private int[] mDataset;
    private LinearLayoutManager layoutManager;
    private SimpleDateFormat formatter;
    private Date date;
    private Paint paint;
    private TextPaint textPaint;
    private Rect rect;


    public void setPaddingleft(int paddingleft) {
        this.paddingleft = paddingleft;
    }

    public WareFormRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int[]dataset, int density) {

        super(context, attrs);

        mDataset = dataset;
        this.density = density;

        setWillNotDraw(false);

        // 设置adapter
        mAdapter = new WareFormAdapter(context,dataset,density);
        setAdapter(mAdapter);

        // 设置横向布局
        layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        setLayoutManager(layoutManager);

        // 设置默认左侧距离为屏幕中间
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        paddingleft = width/2;

        // 初始化变量
        paint = new Paint();
        paint.setColor(drawcolor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setColor(drawcolor);
        textPaint.setTextSize(fontSize);
        textPaint.setAntiAlias(true);

        rect = new Rect();
        formatter = new SimpleDateFormat("mm:ss", Locale.CHINA);
        date = new Date();

    }

    public void setmDataset(int[] mDataset) {
        this.mDataset = mDataset;
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
            // 画字
            drawText(c, startx, paddingx, i);
        }

        for (int i = (int) Math.max(startx / density - 20,0); i <= ( startx + getMeasuredWidth()) / density; i++){
            // 画刻度
            drawKD(c, startx, paddingx, i);

            // 画波形图
            drawLine(c, startx, paddingx, i);
        }

        // 画顶部和底部的两条线
        drawTopLine(c);

        // 中间线
        drawMiddleLine(c);
    }

    private void drawMiddleLine(Canvas c) {
        // 中间线
        rect.set((int)(paddingleft-density), 0 , (int)(paddingleft+density), getMeasuredHeight());
        paint.setColor(centLinecolor);
        c.drawRect(rect, paint);

        // 顶部和底部的两个圆球
        c.drawCircle(paddingleft, radius, radius, paint);
        c.drawCircle(paddingleft, getMeasuredHeight()-radius, radius, paint);
    }

    private void drawTopLine(Canvas c) {
        // 顶部和底部的线
        rect.set(0, radius * 2 , getMeasuredWidth(), (int) (radius * 2+density));
        paint.setColor(topLinecolor);
        c.drawRect(rect, paint);

        rect.set(0, getMeasuredHeight() - radius *2 , getMeasuredWidth(), (int) (getMeasuredHeight() - radius *2-density));
        paint.setColor(topLinecolor);
        c.drawRect(rect, paint);
    }

    private void drawLine(Canvas c, int startx, int paddingx, int i) {

        // 设置画笔颜色
        paint.setColor(linecolor);

        // 波形线x坐标,差了一个pd
        if (i < mDataset.length) {
            int wavex = (int) ((i) * density - startx + paddingx);
            rect.set(wavex, getMeasuredHeight()/2 -mDataset[i] /2 , (int) (wavex + density), getMeasuredHeight()/2 + mDataset[i] /2);
            c.drawRect(rect, paint);
        }
    }

    private void drawKD(Canvas c, int startx, int paddingx, int i) {

        // 设置画笔颜色
        paint.setColor(drawcolor);

        // 刻度x坐标
        int x = (int) ((i-1) * density - startx + paddingx);

        // 10秒位置
        if ((secondPreDp*100*i) % 1000 == 0){

            // 画刻度
            rect.set(x,getMeasuredHeight()-longTerm - 2*radius, (int) (x+ density),getMeasuredHeight()- 2*radius);
            c.drawRect(rect,paint);

        }

        // 5秒位置
        else if ((secondPreDp*100*i) % 500 == 0){
            rect.set(x,getMeasuredHeight()-middleTerm- 2*radius, (int) (x+ density),getMeasuredHeight()- 2*radius);
            c.drawRect(rect,paint);
        }

        // 1秒位置
        else if ((secondPreDp*100*i) % 100 == 0){
            rect.set(x,getMeasuredHeight()-shortTerm- 2*radius, (int) (x+ density),getMeasuredHeight()- 2*radius);
            c.drawRect(rect,paint);
        }
    }

    private void drawText(Canvas c, int startx, int paddingx, int i) {

        // 设置画笔颜色
        paint.setColor(drawcolor);

        // 刻度x坐标
        int x = (int) ((i-1) * density - startx + paddingx);

        // 10秒位置
        if ((secondPreDp*100*i) % 1000 == 0){

            // 格式化时间
            date.setTime((long) (secondPreDp * i * 1000L));
            String dateString = formatter.format(date);

            // 画时间
            textPaint.getTextBounds(dateString, 0, dateString.length(), rect);
            c.drawText(dateString + "", x-rect.width()/2, getMeasuredHeight()-60, textPaint);
        }

    }
}
