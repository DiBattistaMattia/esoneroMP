package it.uniroma2.mp.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.Charset;
import java.util.UUID;

public class SendMessage extends AppCompatActivity implements View.OnClickListener{

    BluetoothConnectionService mBluetoothConnection;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothDevice mBTDevice;

    Button btnSend;
    Button btnStartConnection;
    EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);

        btnSend = findViewById(R.id.btnSend);
        btnStartConnection = findViewById(R.id.btnStartConnection);
        etMessage = findViewById(R.id.etMessage);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.btnSend:
                byte[] bytes = etMessage.getText().toString().getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes);
                break;

            case R.id.btnStartConnection:
                mBluetoothConnection = new BluetoothConnectionService(getApplicationContext());
                startConnection();
                break;

        }

    }

    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d("CONNECTION", "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }

}



