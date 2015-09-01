package com.example.vicce.instrumetronix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Configuration extends Activity {
    Button btn_date,btn_Csensors,btn_Isensors;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        btn_date = (Button)findViewById(R.id.EditDate);
        btn_Csensors = (Button)findViewById(R.id.Csensors);
        btn_Isensors = (Button)findViewById(R.id.Isensors);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Configuration.this, Configuration_Clock.class));
            }
        });
        btn_Csensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Inhabilitado");
            }
        });
        btn_Isensors.setOnClickListener(new View.OnClickListener() {
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
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
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
