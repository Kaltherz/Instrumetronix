package com.example.vicce.instrumetronix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import java.util.Calendar;

public class Configuration_Clock extends Activity {
    private Toast toast;
    Button save_date_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration__clock);

        save_date_button = (Button) findViewById(R.id.save_date);
        save_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Inhabilitado");
            }
        });
    }

    void showToast(String x) {
        if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE)
        {
            toast = Toast.makeText(getApplicationContext(), x, Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            toast.cancel();
            toast = Toast.makeText(getApplicationContext(), x, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration__clock, menu);
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
