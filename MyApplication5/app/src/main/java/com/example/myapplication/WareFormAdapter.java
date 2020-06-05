package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WareFormAdapter extends RecyclerView.Adapter<WareFormAdapter.WaveformViewHolder> {

    private final Context mContext;

    private int mDataWidth;         // 数据区域展示的宽度(像素)

    WareFormAdapter(Context context, int dataWidth) {
        mContext = context;
        mDataWidth = dataWidth;
    }

    @NonNull
    @Override
    public WaveformViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = new View(mContext);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
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


    static class WaveformViewHolder extends RecyclerView.ViewHolder {

        WaveformViewHolder(View v) {
            super(v);

        }
    }

}
