package com.example.xuzhi.easykitchen;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class StartActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences settings = getPreferences(0);
        boolean dbExist = settings.getBoolean("dbExist", false);
        Log.v(LOG_TAG, "dbExist =" + dbExist);
        if(!dbExist) {
            try {
                Utility.copyDataBase(getBaseContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("dbExist", true);
                // Commit the edits!
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
}
