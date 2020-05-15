package it.uniroma2.mp.bluetoothdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Holder();
    }

    class Holder implements View.OnClickListener {
        Button btnTransmitter;
        Button btnReceiver;


        Holder() {
            btnReceiver = findViewById(R.id.btnReceiver);
            btnTransmitter = findViewById(R.id.btnTransmitter);

            btnTransmitter.setOnClickListener(this);
            btnReceiver.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnTransmitter){
                Intent intent = new Intent(MainActivity.this, TransmitterActivity.class);
                startActivity(intent);
            }
            else if(view.getId() == R.id.btnReceiver){
                Intent intent = new Intent(MainActivity.this, ReceiverActivity.class);
                startActivity(intent);
            }
        }
    }
}
