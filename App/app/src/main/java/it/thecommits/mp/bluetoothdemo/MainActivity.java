package it.thecommits.mp.bluetoothdemo;

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
        final Button btnContinue;

        Holder() {
            btnContinue = findViewById(R.id.btnContinue);
            btnContinue.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            Intent intent = new Intent(MainActivity.this, CoreActivity.class);
            startActivity(intent);

        }
    }
}
