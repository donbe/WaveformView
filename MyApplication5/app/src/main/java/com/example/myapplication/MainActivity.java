package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private WareFormRecyclerView recyclerView;
    private WareFormAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (WareFormRecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.density = getDensity();
        recyclerView.paddingleft = 100;


        int length = 3000;
        int[] mydata = new int[length];
        for (int i=0; i<length;i++){
            int max=100,min=1;
            int ran2 = (int) (Math.random() * (max-min) + min);
            mydata[i] = ran2;
        }


        // 设置adapter
        mAdapter = new WareFormAdapter(mydata,getDensity());
        mAdapter.headWidth = 100;
        recyclerView.setAdapter(mAdapter);

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
