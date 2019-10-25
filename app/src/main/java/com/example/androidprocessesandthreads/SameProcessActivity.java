package com.example.androidprocessesandthreads;

import android.os.Bundle;
import android.os.Process;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SameProcessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_layout);

        String processId = getResources().getString(R.string.process_id, Process.myPid());
        ((TextView) findViewById(R.id.processId)).setText(processId);
    }
}
