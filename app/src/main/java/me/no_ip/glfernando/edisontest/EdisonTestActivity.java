package me.no_ip.glfernando.edisontest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class EdisonTestActivity extends Activity {

    private static final String TAG = "EdisonTest";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    // Well known SPP UUID
    //private static final UUID MY_UUID =
    //       UUID.fromString("00001101-0000-1000-8000-00805F9B34FF");
    private static final UUID MY_UUID =
            UUID.fromString("00000000-0000-0000-0000-0000CDAB0000");
    Button on, off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_edison_test);

        on = (Button) findViewById(R.id.button_on);
        off = (Button) findViewById(R.id.button_off);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "No bluetooth device");
            finish();
        }

        on.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("1");
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("0");
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        ArrayList<String> mArrayAdapter = new ArrayList<String>();
        BluetoothDevice device = null;


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice bt : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(bt.getName() + "\n" + bt.getAddress());
                Log.i(TAG, bt.getName() + " " + bt.getAddress());
                //if (bt.getName().equalsIgnoreCase("HC-06")) {
                if (bt.getName().equalsIgnoreCase("BlueZ 5.18")) {
                    //if (bt.getName().equalsIgnoreCase("lenovo-lap-0")) {
                    device = bt;
                    break;
                }
            }
        } else {
            Log.e(TAG, "no device to pair");
            finish();
        }

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Fatal Error In onResume() and socket create failed: " + e.getMessage());
            finish();
        }


        Log.e(TAG, "...Connecting to Remote 1 ..." + device.getName());

        mBluetoothAdapter.cancelDiscovery();

        if (!btSocket.isConnected()) {
            try {
                Log.e(TAG, "No connected, connecting ....");
                btSocket.connect();
                Log.e(TAG, "...Connection established and data link opened...");
            } catch (IOException e) {
                try {
                    Log.e(TAG, "Error connecting :( closing " + e.getMessage());
                    btSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Fatal Error In onResume() and unable to close socket during connection failure" + e2.getMessage());
                    finish();
                }
            }
        }

        Log.e(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Fatal Error In onResume() and output stream creation failed:" + e.getMessage());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        sendData("2");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Fatal Error In onPause() and failed to flush output stream: " + e.getMessage());
                finish();
            }
            outStream = null;
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            Log.e(TAG, "Fatal Error In onPause() and failed to close socket." + e2.getMessage());
            finish();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Sending data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            Log.e(TAG, "Fatal Error" + msg);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edison_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}