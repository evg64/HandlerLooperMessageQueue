package com.example.androidprocessesandthreads;

import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String processId = getResources().getString(R.string.process_id, Process.myPid());
        ((TextView) findViewById(R.id.processId)).setText(processId);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long iteration = 0;
                while (true) {
                    Log.d("MainActivity", "iteration = " + iteration++);
                }
            }
        };
        findViewById(R.id.buttonSameActivity).setOnClickListener(listener);
        findViewById(R.id.buttonAnotherActivity).setOnClickListener(listener);
    }
}
