package com.example.vicce.instrumetronix;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.content.IntentFilter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Set;

public class Bluetooth_Configuration extends Activity  {
    Button  TOn,TOff;
    private BluetoothAdapter BT;
    private Set<BluetoothDevice>pairedDevices;
    private ArrayAdapter<String> btArrayAdapter;
    private ListView listDevicesFound;
    private ArrayAdapter adapter;
    private ArrayAdapter Vadapter;
    private ListView listDevicesVinculated;
    private Toast toast;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth__configuration);

        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        BT = BluetoothAdapter.getDefaultAdapter();
        TOn = (Button) findViewById(R.id.BOn);

        ArrayList list = new ArrayList();
        TOff=(Button)findViewById(R.id.BOff);

        listDevicesFound= (ListView)findViewById(R.id.LFound);
        // listDevicesVinculated = (ListView)findViewById(R.id.LVinculated);
        //Vadapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1);
        adapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1);
        listDevicesFound.setAdapter(adapter);
        //listDevicesVinculated.setAdapter(Vadapter);

        /*if(!BT.isEnabled()) {
            pairedDevices = BT.getBondedDevices();

            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName());
            }
            //Toast.makeText(getApplicationContext(), "Dispositivos vinculados", Toast.LENGTH_SHORT).show();
            final ArrayAdapter adapter =     new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listDevicesVinculated.setAdapter(adapter);

        }
        else
        {
            list.clear();
            gray();
        }        */
        if(BT.isDiscovering()) {
            BT.cancelDiscovery();
        }

    }


    public void on(View v){
        if (!BT.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            //Toast.makeText(getApplicationContext(),"Encendido",Toast.LENGTH_LONG).show();
            gray();
            BT.cancelDiscovery();
        }


    }

    public void off(View v){
        BT.disable();
        Toast.makeText(getApplicationContext(),"Apagado" ,Toast.LENGTH_LONG).show();
        ((Button)findViewById(R.id.search)).setTextColor(0xff000000);
        ((TextView)findViewById(R.id.Vinculated)).setTextColor(0xff000000);
        ((TextView)findViewById(R.id.BFound)).setTextColor(0xff000000);
        adapter.clear();
        //Vadapter.clear();
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

    public void Search(View v){

        if(BT.isEnabled()) {
            // To scan for remote Bluetooth devices
            if(BT.isDiscovering()) {
                showToast("Busqueda cancelada.");

                BT.cancelDiscovery();
                ((Button)findViewById(R.id.search)).setText("Buscar");
            }
            else if (BT.startDiscovery()) {
                showToast("Presione de nuevo para cancelar.");

                ((Button)findViewById(R.id.search)).setText("Cancelar busqueda");
                adapter.clear();
            } else {
                showToast("Error.");

                adapter.clear();
            }
        }
        else
        {
            gray();
        }

    }/*
    search.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            btArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();
        }
    });*/

    private void gray() {
        while (!BT.isEnabled()) {
            ((Button) findViewById(R.id.search)).setTextColor(0xff888888);
            ((TextView) findViewById(R.id.Vinculated)).setTextColor(0x00000000);
            ((TextView) findViewById(R.id.BFound)).setTextColor(0x00000000);
            break;
        }
        while (BT.isEnabled()) {
            ((Button) findViewById(R.id.search)).setTextColor(0xff000000);
            ((TextView) findViewById(R.id.Vinculated)).setTextColor(0xff000000);
            ((TextView) findViewById(R.id.BFound)).setTextColor(0xff000000);
            adapter.clear();
            //Vadapter.clear();

            break;
        }
        //enable

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Whenever a remote Bluetooth device is found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                //adapter.add(bluetoothDevice.getName() + "\n"
                // + bluetoothDevice.getAddress());
                adapter.add(bluetoothDevice.getName());
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth__configuration, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //this.registerReceiver(broadcastReceiver, filter);
        gray();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //this.unregisterReceiver(broadcastReceiver);
        gray();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case R.id.action_erase_vinculated:

                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}