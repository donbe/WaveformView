package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static android.view.View.OVER_SCROLL_NEVER;

public class MainActivity extends AppCompatActivity {

    private WareFormRecyclerView recyclerView;
    private WareFormAdapter mAdapter;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        int length = 3000;
        int[] mydata = new int[length];
        for (int i=0; i<length;i++){
            int max=100,min=1;
            int ran2 = (int) (Math.random() * (max-min) + min);
            mydata[i] = ran2;
        }


        ConstraintLayout layout = findViewById(R.id.content_main);

        recyclerView = new WareFormRecyclerView(this,null,mydata, (int) getDensity());;
        recyclerView.paddingleft = 500;
        recyclerView.setId(View.generateViewId());
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        layout.addView(recyclerView);

        // 设置约束
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(recyclerView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 500);
        set.constrainHeight(recyclerView.getId(),280);
        set.applyTo(layout);


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

    private float getDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }
}
