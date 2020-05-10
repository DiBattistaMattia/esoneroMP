package it.uniroma1.mp.bluetoothdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        setTitle("Receiver");
    }
}
