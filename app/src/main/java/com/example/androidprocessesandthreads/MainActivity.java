package com.example.androidprocessesandthreads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidprocessesandthreads.handlerpractice.HandlerActivity;

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
                final Class<? extends Activity> activityClass;
                switch (v.getId()) {
                    case R.id.buttonSameActivity:
                        activityClass = SameProcessActivity.class;
                        break;
                    case R.id.buttonAnotherActivity:
                        activityClass = AnotherProcessActivity.class;
                        break;
                    case R.id.handlerActivity:
                    default:
                        activityClass = HandlerActivity.class;
                }
                startActivity(new Intent(MainActivity.this, activityClass));
            }
        };
        findViewById(R.id.buttonSameActivity).setOnClickListener(listener);
        findViewById(R.id.buttonAnotherActivity).setOnClickListener(listener);
        findViewById(R.id.handlerActivity).setOnClickListener(listener);
    }
}
