package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WareFormAdapter extends RecyclerView.Adapter<WareFormAdapter.WaveformViewHolder> {

    public int headWidth = 0;

    public int color = 0xff00ff00;
    private int[] mDataset;
    private float mDensity;
    WareFormAdapter(int[] myDataset, float density) {
        mDataset = myDataset;
        mDensity = density;
    }

    @NonNull
    @Override
    public WaveformViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.wave_item, parent, false);
            return new WaveformViewHolder(v);

    }

    @Override
    public void onBindViewHolder(WaveformViewHolder holder, int position) {


            // 设置宽度为一个dp
            ViewGroup.LayoutParams params = holder.layout.getLayoutParams();
            params.width = (int) mDensity;
            holder.layout.setLayoutParams(params);

            // 设置高度
            ViewGroup.LayoutParams itemParams  = holder.item.getLayoutParams();
            itemParams.height = mDataset[position];
            holder.item.setLayoutParams(itemParams);

            // 设置颜色
            holder.item.setBackgroundColor(color);


    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


    static class WaveformViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout;
        View item;

        WaveformViewHolder(RelativeLayout v) {
            super(v);
            layout = v;
            item = v.findViewById(R.id.item);
        }
    }

}
