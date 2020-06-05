package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WareFormAdapter extends RecyclerView.Adapter<WareFormAdapter.WaveformViewHolder> {

    private final Context mContext;

    private int[] mDataset;
    private float mDensity;

    WareFormAdapter(Context context, int[] myDataset, float density) {

        mContext = context;
        mDataset = myDataset;
        mDensity = density;
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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        // 设置宽度
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = (int) (width + mDensity * mDataset.length) ;
        holder.itemView.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        return 1;
    }


    static class WaveformViewHolder extends RecyclerView.ViewHolder {

        WaveformViewHolder(View v) {
            super(v);

        }
    }

}
