package com.example.vicce.instrumetronix;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends Activity {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {// Create a BroadcastReceiver for ACTION_FOUND
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {// Whenever a remote Bluetooth device is found
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);// Get the BluetoothDevice object from the Intent
                adapter.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());// Add the name and address to an array adapter to show in a ListView
            }
        }
    };
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton toggleButton;
    private Button Clock;
    private ListView listview;
    private ArrayAdapter adapter;
    BluetoothSocket btSocket = null;

    private static final int ENABLE_BT_REQUEST_CODE = 1;
    private static final int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private static final int DISCOVERABLE_DURATION = 300;
    public static String EXTRA_ADDRESS = "device_address";
    String address = null;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        Clock = (Button)findViewById(R.id.DConfig);
        listview = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(myListClickListener);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onToggleClicked(View view) {
        adapter.clear();
        ToggleButton toggleButton = (ToggleButton) view;
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Su dispositivo no cuenta con Bluetooth.",Toast.LENGTH_SHORT).show();// Device does not support Bluetooth
            toggleButton.setChecked(false);
        } else {
            if (toggleButton.isChecked()){ // to turn on bluetooth
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);// A dialog will appear requesting user permission to enable Bluetooth
                    startActivityForResult(enableBluetoothIntent, ENABLE_BT_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth ya esta activado.", Toast.LENGTH_SHORT).show();
                    discoverDevices();// To discover remote Bluetooth devices
                }
            } else { // Turn off bluetooth
                bluetoothAdapter.disable();
                adapter.clear();
                Toast.makeText(getApplicationContext(), "Bluetooth apagado.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            new ConnectBT().execute(); //Call the class to connect
/*
            // Make an intent to start next activity.
            Intent i = new Intent(MainActivity.this, ledControl.class);

            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);*/
        }
    };
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT_REQUEST_CODE) { // Bluetooth successfully enabled!
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth habilitado.", Toast.LENGTH_SHORT).show();
                discoverDevices();// To discover remote Bluetooth devices
            } else { // RESULT_CANCELED as user refused or failed to enable Bluetooth
                Toast.makeText(getApplicationContext(), "Bluetooth no habilitado.", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);// Turn off togglebutton
            }
        }
    }

    public void onClick(View v){
        Intent intent = new Intent(this, Configuration_Clock.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void buttonOnClick(View v) {
        {
            if (btSocket != null) {
                try {
                    btSocket.getOutputStream().write("Presionado\n".toString().getBytes());
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    protected void discoverDevices(){

        if (bluetoothAdapter.startDiscovery()) {// To scan for remote Bluetooth devices
            Toast.makeText(getApplicationContext(), "Escaneando por dispositivos...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Escaneo ha fallado.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

// other code

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(MainActivity.this, "Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_SHORT).show();

                finish();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Connected.", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
