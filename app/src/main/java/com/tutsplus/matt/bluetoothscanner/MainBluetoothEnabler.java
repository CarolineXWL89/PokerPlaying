package com.tutsplus.matt.bluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Set;


public class MainBluetoothEnabler extends ActionBarActivity implements DeviceListFragment.OnFragmentInteractionListener  {


    private DeviceListFragment mDeviceListFragment;
    private BluetoothAdapter BTAdapter;


    public static int REQUEST_BLUETOOTH = 1;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list); //blah fix the R reference file!!!

        //TODO Check if bluetooth is enabled

        FragmentManager fragmentManager = getSupportFragmentManager();
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        mDeviceListFragment = DeviceListFragment.newInstance(BTAdapter);
        fragmentManager.beginTransaction().replace(R.id.container, mDeviceListFragment).commit();
        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        Log.d("DEVICELIST", "Super called for DeviceListFragment onCreate\n");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<DeviceItem>();

        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false", device);
                deviceItemList.add(newDevice);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
    public void onFragmentInteraction(String id) {

    }
    @Override
    public void onPause() {
        super.onPause();
        if(mDeviceListFragment.getServerConnectThread()!=null){
            mDeviceListFragment.getServerConnectThread().closeConnect();
        }
    }
}
