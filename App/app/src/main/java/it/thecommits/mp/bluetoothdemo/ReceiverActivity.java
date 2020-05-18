package it.thecommits.mp.bluetoothdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.UUID;

public class ReceiverActivity extends AppCompatActivity implements View.OnClickListener{
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        setTitle("Receiver");
    }
    @Override
    public void onClick(View view) {
        startConnection();
    }
    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d("CONNECTION", "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }
}
