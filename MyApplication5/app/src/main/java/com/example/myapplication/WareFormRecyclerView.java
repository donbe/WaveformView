package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
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

@SuppressLint("ViewConstructor")
public class WareFormRecyclerView extends RecyclerView {

    public float mSecondPreDp = 0.05f;      // 一个dp多少秒
    public int mDrawcolor = 0xff3D4057;     // 刻度颜色
    public int mLinecolor = 0xff779FD7;     // 波形图线的颜色
    public int mTopLinecolor = 0xff63647C;     // 顶部和底部线颜色
    public int mCentLinecolor = 0xffffffff;     // 中线
    public int mFontSize = 30;              // 刻度字体大小
    public int mShortTerm = 15;             // 1秒钟刻度线长度(像素)
    public int mMiddleTerm = 22;            // 5秒钟刻度线长度(像素)
    public int mLongTerm = 28;             // 10秒钟刻度线长度(像素)
    public int mGradientStartColor = 0x00171C35;     // 渐变的起始颜色
    public int mGradientEndColor = 0x332AAEF3;       // 渐变的结束颜色
    public int mTopLineHeight = 1;                   // 顶部，底部的线的高度（DP）
    public int mPaddingleft = 0;                    // 刻度的起始坐标(像素)
    public int[] mDataset;
    private float mDensity = 1;             // 手机屏幕密度
    private int mRadius = 8;
    private SimpleDateFormat mFormatter;
    private Date mDate;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private Paint mGradientPaint;
    private Rect mRect;


    public WareFormRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int[]dataset, int density) {

        super(context, attrs);

        mDataset = dataset;
        this.mDensity = density;

        setWillNotDraw(false);

        // 设置adapter
        WareFormAdapter mAdapter = new WareFormAdapter(context, dataset.length * density);
        setAdapter(mAdapter);

        // 设置横向布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);

        // 设置默认左侧距离为屏幕中间
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        mPaddingleft = width/2;

        // 初始化变量
        mPaint = new Paint();
        mPaint.setColor(mDrawcolor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mDrawcolor);
        mTextPaint.setTextSize(mFontSize);
        mTextPaint.setAntiAlias(true);

        mGradientPaint = new Paint();

        mRect = new Rect();
        mFormatter = new SimpleDateFormat("mm:ss", Locale.CHINA);
        mDate = new Date();

    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        // 滚动条x平移量
        int scrollOffsetX= computeHorizontalScrollOffset();
        // 用来计算刻度开始的时间
        int startx = Math.max(scrollOffsetX - mPaddingleft,0);
        // 刻度开始画的起始坐标
        int paddingx = Math.max(mPaddingleft - scrollOffsetX,0);


        for (int i = (int) Math.max(startx / mDensity - 20,0); i <= ( startx + getMeasuredWidth()) / mDensity; i++){
            // 画字
            drawText(c, startx, paddingx, i);
        }

        for (int i = (int) Math.max(startx / mDensity - 20,0); i <= ( startx + getMeasuredWidth()) / mDensity; i++){
            // 画刻度
            drawKD(c, startx, paddingx, i);

            // 画波形图
            drawLine(c, startx, paddingx, i);
        }

        // 画顶部和底部的两条线
        drawTopLine(c);

        // 画渐变
        drawGradient(c, scrollOffsetX);

        // 中间线
        drawMiddleLine(c);
    }

    /*画渐变覆盖层*/
    private void drawGradient(Canvas c, int scrollOffsetX) {

        int x = (int) (mPaddingleft + Math.max(0,mDataset.length * mDensity - (getMeasuredWidth() >> 1)) - scrollOffsetX);
        int x1 = (int) (mPaddingleft +mDataset.length * mDensity - scrollOffsetX);
        int y = (int) (2* mRadius + mTopLineHeight * mDensity);
        int y1 = (int) (getMeasuredHeight()-2* mRadius - mTopLineHeight * mDensity);

        LinearGradient shader = new LinearGradient(x, y, x1, y1, mGradientStartColor, mGradientEndColor, Shader.TileMode.CLAMP);
        mGradientPaint.setShader(shader);
        mRect.set(x, y , x1, y1);
        c.drawRect(mRect, mGradientPaint);

    }

    /*画正中间的固定不动的指针*/
    private void drawMiddleLine(Canvas c) {

        // 中间线
        mRect.set((int)(mPaddingleft - mDensity), 0 , (int)(mPaddingleft + mDensity), getMeasuredHeight());
        mPaint.setColor(mCentLinecolor);
        c.drawRect(mRect, mPaint);

        // 顶部和底部的两个圆球
        c.drawCircle(mPaddingleft, mRadius, mRadius, mPaint);
        c.drawCircle(mPaddingleft, getMeasuredHeight()- mRadius, mRadius, mPaint);

    }

    /*画顶部和底部两条辅助线*/
    private void drawTopLine(Canvas c) {

        // 顶部和底部的线
        mRect.set(0, mRadius * 2 , getMeasuredWidth(), (int) (mRadius * 2+ mTopLineHeight * mDensity));
        mPaint.setColor(mTopLinecolor);
        c.drawRect(mRect, mPaint);

        mRect.set(0, getMeasuredHeight() - mRadius *2 , getMeasuredWidth(), (int) (getMeasuredHeight() - mRadius *2- mTopLineHeight * mDensity));
        mPaint.setColor(mTopLinecolor);
        c.drawRect(mRect, mPaint);

    }

    /*画波形图*/
    private void drawLine(Canvas c, int startx, int paddingx, int i) {

        // 设置画笔颜色
        mPaint.setColor(mLinecolor);

        // 波形线x坐标,差了一个pd
        if (i < mDataset.length) {
            int wavex = (int) ((i) * mDensity - startx + paddingx);
            mRect.set(wavex, getMeasuredHeight()/2 -mDataset[i] /2 , (int) (wavex + mDensity), getMeasuredHeight()/2 + mDataset[i] /2);
            c.drawRect(mRect, mPaint);
        }
    }

    /*画刻度线*/
    private void drawKD(Canvas c, int startx, int paddingx, int i) {

        // 设置画笔颜色
        mPaint.setColor(mDrawcolor);

        // 刻度x坐标
        int x = (int) ((i-1) * mDensity - startx + paddingx);

        // 10秒位置
        if ((mSecondPreDp *100*i) % 1000 == 0){

            // 画刻度
            mRect.set(x,getMeasuredHeight()- mLongTerm - 2* mRadius, (int) (x+ mDensity),getMeasuredHeight()- 2* mRadius);
            c.drawRect(mRect, mPaint);

        }

        // 5秒位置
        else if ((mSecondPreDp *100*i) % 500 == 0){
            mRect.set(x,getMeasuredHeight()- mMiddleTerm - 2* mRadius, (int) (x+ mDensity),getMeasuredHeight()- 2* mRadius);
            c.drawRect(mRect, mPaint);
        }

        // 1秒位置
        else if ((mSecondPreDp *100*i) % 100 == 0){
            mRect.set(x,getMeasuredHeight()- mShortTerm - 2* mRadius, (int) (x+ mDensity),getMeasuredHeight()- 2* mRadius);
            c.drawRect(mRect, mPaint);
        }
    }

    /*画显示整10秒位置的时间文本*/
    private void drawText(Canvas c, int startx, int paddingx, int i) {

        // 设置画笔颜色
        mPaint.setColor(mDrawcolor);

        // 刻度x坐标
        int x = (int) ((i-1) * mDensity - startx + paddingx);

        // 10秒位置
        if ((mSecondPreDp *100*i) % 1000 == 0){

            // 格式化时间
            mDate.setTime((long) (mSecondPreDp * i * 1000L));
            String dateString = mFormatter.format(mDate);

            // 画时间
            mTextPaint.getTextBounds(dateString, 0, dateString.length(), mRect);
            c.drawText(dateString + "", x- mRect.width()/2, getMeasuredHeight()-(mFontSize + mLongTerm), mTextPaint);
        }

    }
}
