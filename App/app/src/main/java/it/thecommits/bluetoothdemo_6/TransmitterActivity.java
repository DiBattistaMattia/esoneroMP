package it.thecommits.bluetoothdemo_6;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class TransmitterActivity extends AppCompatActivity {
    private static final String TAG = "TransmitterActivity";

    private BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTPairedDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> mBTDiscoveredDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    private BluetoothConnectionService mBluetoothConnection;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private BluetoothDevice mBTDevice;

    Holder holder;
    StringBuilder messages;
    TextView tvRecevedMessage;


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                for(BluetoothDevice dev: mBTDiscoveredDevices){
                    if(device.getAddress().compareTo(dev.getAddress()) == 0)
                        return;
                }
                mBTDiscoveredDevices.add(device);
                holder.addDeviceToList(device, false);
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                    holder.activateMessaging();
                }
                //case2: creating a bond
                else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                else if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmitter);
        holder = new Holder();
        retrievePairedDevices();
        messages = new StringBuilder();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            Log.d(INPUT_SERVICE,intent.getStringExtra("theMessage") );
            messages.append(text + "\n");
            tvRecevedMessage.setText(messages);
        }
    };




    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }


    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        //check BT permissions in manifest
        checkBTPermissions();

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        else {
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    private void retrievePairedDevices(){
        Set<BluetoothDevice> set = mBluetoothAdapter.getBondedDevices();
        mBTPairedDevices.addAll(set);
        for(BluetoothDevice dev: set){
            holder.addDeviceToList(dev, true);
        }
    }

    private void connectToDevice(BluetoothDevice device){
        mBluetoothConnection = new BluetoothConnectionService(TransmitterActivity.this);
        mBluetoothConnection.startClient(device, MY_UUID_INSECURE);
    }

    private void sendMessage(String message){
        mBluetoothConnection.write(message.getBytes());
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    class Holder implements OnItemClickListener, View.OnClickListener {
        Button btnEnableDisable_Discoverable;
        Button btnSendMessage;
        EditText etUserMessage;
        ListView lvNewDevices;
        ListView lvPairedDevices;

        public Holder (){
            Button btnOnOff = findViewById(R.id.btnONOFF);
            btnEnableDisable_Discoverable = findViewById(R.id.btnDiscoverable_on_off);
            lvNewDevices = findViewById(R.id.lvNewDevices);
            lvPairedDevices = findViewById(R.id.lvPairedDevices);
            btnSendMessage = findViewById(R.id.btnSend);
            etUserMessage = findViewById(R.id.etMessage);

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            lvNewDevices.setOnItemClickListener(this);
            lvPairedDevices.setOnItemClickListener(this);

            btnOnOff.setOnClickListener(this);
            etUserMessage.setVisibility(View.INVISIBLE);
            btnSendMessage.setVisibility(View.INVISIBLE);

        }

        public void addDeviceToList(BluetoothDevice device, boolean isPaired){
            Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
            if(!isPaired) {
                mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_adapter_view, mBTDiscoveredDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
            else {
                mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_adapter_view, mBTPairedDevices);
                lvPairedDevices.setAdapter(mDeviceListAdapter);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: click detected");
            BluetoothDevice device;
            if(parent.getId() == R.id.lvNewDevices) {
                Log.d(TAG, "onItemClick: connecting to discovered device");
                device = mBTDiscoveredDevices.get(position);
                connectToDevice(device);
                setTitle(device.getName());
                activateMessaging();

            }
            else if(parent.getId() == R.id.lvPairedDevices) {
                Log.d(TAG, "onItemClick: connecting to paired device");
                device = mBTPairedDevices.get(position);
                connectToDevice(device);
                setTitle(device.getName());
                activateMessaging();

            }
        }

        @Override
        public void OnClick(String deviceAddress) {

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnONOFF:
                    Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                    enableDisableBT();
                    break;
                case R.id.btnSend:
                    if(etUserMessage.getText() != null && !etUserMessage.getText().toString().isEmpty()){
                        String message = etUserMessage.getText().toString();
                        Log.d(TAG, "onClick: sending message to the other device");
                        sendMessage(message);
                    }else{
                        Toast toast = null;
                        toast.makeText(getApplicationContext(), "Before to send, write something",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }

        }

        public void activateMessaging() {
            etUserMessage.setVisibility(View.VISIBLE);
            btnSendMessage.setVisibility(View.VISIBLE);
            lvPairedDevices.setVisibility(View.GONE);
            lvNewDevices.setVisibility(View.GONE);
        }
    }
}
