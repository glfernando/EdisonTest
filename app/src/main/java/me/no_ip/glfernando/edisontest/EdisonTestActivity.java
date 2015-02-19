package me.no_ip.glfernando.edisontest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class EdisonTestActivity extends ActionBarActivity implements NavigationDrawerFragment.DrawerFragmentComm, EdisonComm {

    private static final String TAG = "EdisonTest";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket = null;
    public OutputStream outStream = null;
    private boolean connected = false;
    private Set<BluetoothDevice> pairedDevices;
    private static BluetoothDevice device;
    private static int currentPosition = -1;

    // Well known SPP UUID
    //private static final UUID MY_UUID =
    //       UUID.fromString("00001101-0000-1000-8000-00805F9B34FF");
    private static final UUID MY_UUID =
            UUID.fromString("00000000-0000-0000-0000-0000CDAB0000");

    public void onClickOn(View v) {
        sendData("1");
    }

    public void onClickOff(View v) {
        sendData("0");
    }

    public void onClickStatus(View v) {
        //
    }

    public void connect() {
        TextView textView = (TextView) findViewById(R.id.textViewStatus);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerBt);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Fatal Error In onResume() and socket create failed: " + e.getMessage());
            finish();
        }

        Log.e(TAG, "...Connecting to Remote ..." + device.getName());



        if (!btSocket.isConnected()) {
            try {
                Log.e(TAG, "No connected, connecting ....");
                btSocket.connect();
                Log.e(TAG, "...Connection established and data link opened...");
            } catch (IOException e) {
                try {
                    Log.e(TAG, "Error connecting :( closing " + e.getMessage());
                    btSocket.close();
                    Toast.makeText(getApplicationContext(), "Error connecting", Toast.LENGTH_SHORT).show();
                    spinner.setSelection(0);
                    currentPosition = -1;
                    return;
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
        connected = true;
        textView.setText("Connected");
    }

    public void disconnect() {
        TextView textView = (TextView) findViewById(R.id.textViewStatus);

        sendData("2");

        Log.d(TAG, "disconnecting from " + device.getName());

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
        connected = false;
        textView.setText("Disconnected");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_edison_test);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerBt);
        TextView status = (TextView) findViewById(R.id.textViewStatus);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment  = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.left_drawer);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
        drawerFragment.setUp((DrawerLayout)findViewById(R.id.drawer_layout), toolbar);

        status.setText(connected ? "Connected" : "Disconnected");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "No bluetooth device");
            finish();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position-- == 0) {
                    if (connected)
                        disconnect();
                    currentPosition = -1;
                    return;
                }

                int i = 0;
                for (BluetoothDevice bt : pairedDevices) {
                    if (i == position) {
                        device = bt;
                        break;
                    }
                    i++;
                }

                if (currentPosition >= 0 && currentPosition != i)
                    disconnect();
                currentPosition = i;
                connect();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "onNothingSelected", Toast.LENGTH_SHORT).show();
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
        ArrayList<String> mArrayList = new ArrayList<String>();
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mArrayList);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerBt);
        mArrayAdapter.add("Select BT device");


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice bt : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayList.add(bt.getName() + ":" + bt.getAddress());
                Log.i(TAG, bt.getName() + " " + bt.getAddress());
            }
        } else {
            Log.e(TAG, "no device to pair");
            finish();
        }

        mBluetoothAdapter.cancelDiscovery();

        spinner.setAdapter(mArrayAdapter);

        if (currentPosition >= 0) {
            Log.d(TAG, "current positon " + currentPosition);
            //connect();
            spinner.setSelection(currentPosition + 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (connected)
            disconnect();
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

        if (!connected) {
            Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }

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

    @Override
    public void updateFragment(int position) {
        if (position == 1) {
            Fragment frag = new GpioFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, frag, "gpio");
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Toast.makeText(this, "No implemented", Toast.LENGTH_SHORT).show();
        }
    }
}