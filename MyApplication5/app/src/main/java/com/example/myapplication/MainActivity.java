package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static android.view.View.OVER_SCROLL_NEVER;

public class MainActivity extends AppCompatActivity implements WareFormRecyclerView.WareFormRecyclerViewListener {

    private WareFormRecyclerView recyclerView;
    private WareFormRecyclerView.WareFormAdapter mAdapter;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        int length = 100;
        final int[] mydata = new int[length];
        for (int i=0; i<length;i++){
            int max=50,min=1;
            int ran2 = (int) (Math.random() * (max-min) + min);
            mydata[i] = ran2;
        }


        ConstraintLayout layout = findViewById(R.id.content_main);

        recyclerView = new WareFormRecyclerView(this,null,null);
        recyclerView.setId(View.generateViewId());
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        recyclerView.listener = this;

        layout.addView(recyclerView);

        // 设置约束
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(recyclerView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 500);
        set.constrainHeight(recyclerView.getId(),200);
        set.applyTo(layout);


        Button btn = findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setmDataset(mydata);
                recyclerView.invalidate();
            }
        });

        Button seekbtn = findViewById(R.id.seek);
        seekbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToMilliSecond(1000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onScrolled(int dx, int millisecond) {
        ImageView v = findViewById(R.id.imageView);


        // 设置约束
        ConstraintLayout layout = findViewById(R.id.content_main);

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(v.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, dx);
        set.applyTo(layout);

        Log.d("donbe", "onScrolled: "+ dx);
    }
}
