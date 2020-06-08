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
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@SuppressLint("ViewConstructor")
public class WareFormRecyclerView extends RecyclerView {

    private final WareFormAdapter mAdapter;
    private final WaveformViewLayout mLayoutManager;
    public int mMScondPreDp = 50;      // 一个dp多少毫秒,这个值必须要能被1000整除
    public int mPaddingleft = 0;                    // 刻度的起始坐标(像素)
    public int mDrawcolor = 0xff3D4057;     // 刻度颜色
    public int mFontSize = 30;              // 刻度字体大小
    public int mShortTerm = 15;             // 1秒钟刻度线长度(像素)
    public int mMiddleTerm = 22;            // 5秒钟刻度线长度(像素)
    public int mLongTerm = 28;             // 10秒钟刻度线长度(像素)
    public int mLinecolor = 0xff779FD7;         // 波形图线的颜色
    public int mCentLinecolor = 0xffffffff;     // 中线颜色
    public int mRadius = 8;                //中线顶部底部的圆球半径
    public int mTopLineHeight = 1;             // 顶部，底部的线的高度（DP）
    public int mTopLinecolor = 0xff63647C;     // 顶部和底部线颜色
    public int mGradientStartColor = 0x00171C35;     // 渐变的起始颜色
    public int mGradientEndColor = 0x332AAEF3;       // 渐变的结束颜色
    public WareFormRecyclerViewListener listener ; //滚动监听
    // 时间格式
    public SimpleDateFormat mFormatter = new SimpleDateFormat("mm:ss", Locale.CHINA);
    private Context mContext;
    private ArrayList<Short> mDataset = new ArrayList<Short>();                      //波形图数据，每一个元素对应一个dp
    private int mDensity;             // 手机屏幕密度;
    private Date mDate = new Date();
    // 笔触
    private Paint mPaint = new Paint();
    private TextPaint mTextPaint = new TextPaint();
    private Paint mGradientPaint = new Paint();
    private Rect mRect = new Rect();
    private int mCurrentScrollOffsetx; // 当前毫秒数
    private int mOnceOffsetx; // 设置数据的时候，设置的使用一次的变量，因为在不停的setmdataset的时候，computeHorizontalScrollOffset()函数计算始终返回0

    public WareFormRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, ArrayList<Short>dataset) {

        super(context, attrs);

        mContext = context;
        if (dataset != null) mDataset = dataset;
        mDensity = getDensity();

        setWillNotDraw(false);

        // 设置adapter
        mAdapter = new WareFormAdapter(context, mDataset.size() * mDensity);
        setAdapter(mAdapter);

        // 设置横向布局
        mLayoutManager = new WaveformViewLayout(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(mLayoutManager);

        // 设置默认左侧距离为屏幕中间
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        mPaddingleft = width/2;

        // 初始化变量
        mPaint.setColor(mDrawcolor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mTextPaint.setColor(mDrawcolor);
        mTextPaint.setTextSize(mFontSize);
        mTextPaint.setAntiAlias(true);

        // 监听数据变动
        mAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                // 移动到底部
                mLayoutManager.scrollToPositionWithOffset(0,-mDataset.size() * mDensity);

                // 重新绘制波形图
                WareFormRecyclerView.this.invalidate();
            }
        });

    }


    public ArrayList<Short> getmDataset() {
        return mDataset;
    }

    /*
     * 改变数据后，会发生以下几件事情
     * 1 重新ondraw
     * 2 recycler reloaddata
     * 3 滚动recyclerview到底部
     * */
    public void setmDataset(ArrayList<Short> mDataset) {

        this.mDataset = mDataset;

        // 计算应该移动多少x周的间距
        mOnceOffsetx =  mDataset.size() * mDensity;

        // 刷新recycleview
        mAdapter.mDataWidth = mDataset.size() * mDensity;
        mAdapter.notifyDataSetChanged();
    }

    /*设置滚动到某个时间点*/
    public void scrollToMilliSecond(int millisecond){

        int dp = millisecond / mMScondPreDp;
        mLayoutManager.scrollToPositionWithOffset(0,-dp * mDensity);
    }

    /*获取滑动状态*/
    public boolean getScrollEnabled() {
        return mLayoutManager.getScrollEnabled();
    }

    /*设置是否可以滑动*/
    public void setScrollEnabled(boolean flag) {
        mLayoutManager.setScrollEnabled(flag);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        // 滚动条x平移量
        int scrollOffsetX= computeHorizontalScrollOffset();
        if (mOnceOffsetx >0) {
            scrollOffsetX = mOnceOffsetx;
            mOnceOffsetx = 0;
        }

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


        if (mCurrentScrollOffsetx != scrollOffsetX){
            mCurrentScrollOffsetx = scrollOffsetX;

            // 回调
            if (listener != null ) listener.onScrolled(scrollOffsetX, getCurrentTime());
        }
    }

    /*获取当前的毫秒数*/
    public int getCurrentTime(){
        int scrollOffsetX= computeHorizontalScrollOffset();
        return (int) (Math.ceil(scrollOffsetX/3.0)* mMScondPreDp);
    }

    /*画渐变覆盖层*/
    private void drawGradient(Canvas c, int scrollOffsetX) {

        int x = (int) (mPaddingleft + Math.max(0,mDataset.size() * mDensity - (getMeasuredWidth() >> 1)) - scrollOffsetX);
        int x1 = (int) (mPaddingleft +mDataset.size() * mDensity - scrollOffsetX);
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
        if (i < mDataset.size()) {
            int wavex = (int) ((i) * mDensity - startx + paddingx);
            mRect.set(wavex, getMeasuredHeight()/2 - (int)mDataset.get(i) /2 , (int) (wavex + mDensity), getMeasuredHeight()/2 + (int)mDataset.get(i) /2);
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
        if ((mMScondPreDp*i) % 10000 == 0){

            // 画刻度
            mRect.set(x,getMeasuredHeight()- mLongTerm - 2* mRadius, (int) (x+ mDensity),getMeasuredHeight()- 2* mRadius);
            c.drawRect(mRect, mPaint);

        }

        // 5秒位置
        else if ((mMScondPreDp*i) % 5000 == 0){
            mRect.set(x,getMeasuredHeight()- mMiddleTerm - 2* mRadius, (int) (x+ mDensity),getMeasuredHeight()- 2* mRadius);
            c.drawRect(mRect, mPaint);
        }

        // 1秒位置
        else if ((mMScondPreDp*i) % 1000 == 0){
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
        if ((mMScondPreDp*i) % 10000 == 0){

            // 格式化时间
            mDate.setTime((long) (mMScondPreDp * i));
            String dateString = mFormatter.format(mDate);

            // 画时间
            mTextPaint.getTextBounds(dateString, 0, dateString.length(), mRect);
            c.drawText(dateString + "", x- mRect.width()/2, getMeasuredHeight()-(mFontSize + mLongTerm), mTextPaint);
        }
    }

    /*获取屏幕密度*/
    private int getDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) dm.density;
    }

    /*滚动监听*/
    public interface WareFormRecyclerViewListener  {

        /*
         * 滚动时回调
         *
         * @param 横向移动的距离（像素）
         * @param 当前的毫秒数
         * */
        public void onScrolled( int dx, int millisecond);

    }

    public static class WareFormAdapter extends Adapter<WareFormAdapter.WaveformViewHolder> {

        private final Context mContext;

        public int mDataWidth;         // 数据区域展示的宽度(像素)

        WareFormAdapter(Context context, int dataWidth) {
            mContext = context;
            mDataWidth = dataWidth;
        }

        @NonNull
        @Override
        public WaveformViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = new View(mContext);
            LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            return new WaveformViewHolder(v);

        }

        @Override
        public void onBindViewHolder(WaveformViewHolder holder, int position) {

            // 获取屏幕宽度
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            // 设置cell的宽度
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.width = (int) (width + mDataWidth) ;
            holder.itemView.setLayoutParams(params);

        }

        @Override
        public int getItemCount() {
            // 只有一个cell就够了
            return 1;
        }


        static class WaveformViewHolder extends ViewHolder {

            WaveformViewHolder(View v) {
                super(v);

            }
        }

    }

    static class WaveformViewLayout extends LinearLayoutManager{
        private boolean isScrollEnabled = true;

        WaveformViewLayout(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        boolean getScrollEnabled() {
            return isScrollEnabled;
        }

        void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollHorizontally() {
            return isScrollEnabled && super.canScrollHorizontally();
        }


    }
}
