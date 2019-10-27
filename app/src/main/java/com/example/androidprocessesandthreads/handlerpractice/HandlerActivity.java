package com.example.androidprocessesandthreads.handlerpractice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidprocessesandthreads.R;
import com.example.androidprocessesandthreads.view.FinanceProgressView;

import java.lang.ref.WeakReference;

/**
 * Активити с view-прогрессом выполненной работы
 */
public class HandlerActivity extends AppCompatActivity {

    private enum State {
        /**
         * Работа не выполняется
         */
        OFF,
        /**
         * Работа выполняется
         */
        RUNNING,
        /**
         * Работа приостановлена
         */
        PAUSED
    }

    private Button mStart;
    private Button mPause;
    private Button mCancel;
    private FinanceProgressView mProgressView;
    private BackgroundWorker mWorker;
    private State mState;
    private Handler mMainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        initViews();
        setState(State.OFF);
        initBackgroundWorker();
        setOnClickListeners();
    }

    @Override
    protected void onPause() {
        if (mState == State.RUNNING) {
            mWorker.pauseWork();
            setState(State.PAUSED);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            mWorker.quit();
            mWorker = null;
        }
        super.onDestroy();
    }

    private void initBackgroundWorker() {
        mWorker = new BackgroundWorker("BackgroundWorker");
        mMainThreadHandler = new MainThreadHandler(this);
        mWorker.setClient(mMainThreadHandler);
        mWorker.start();
    }

    private void setOnClickListeners() {
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorker.startWork();
                setState(State.RUNNING);
            }
        });
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final State newState;
                if (mState == State.PAUSED) {
                    mWorker.resumeWork();
                    newState = State.RUNNING;
                } else {
                    mWorker.pauseWork();
                    newState = State.PAUSED;
                }
                setState(newState);
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorker.cancelWork();
                setState(State.OFF);
            }
        });
    }

    private void initViews() {
        mStart = findViewById(R.id.buttonStart);
        mPause = findViewById(R.id.buttonPause);
        mCancel = findViewById(R.id.buttonCancel);
        mProgressView = findViewById(R.id.progress);
        mProgressView.setProgress(0);
    }

    private void setState(@NonNull State state) {
        mState = state;
        switch (state) {
            case OFF:
                mStart.setEnabled(true);
                mPause.setEnabled(false);
                mCancel.setEnabled(false);
                mPause.setText(getResources().getString(R.string.pause));
                break;
            case RUNNING:
                mStart.setEnabled(false);
                mPause.setEnabled(true);
                mCancel.setEnabled(true);
                mPause.setText(getResources().getString(R.string.pause));
                break;
            case PAUSED:
                mStart.setEnabled(false);
                mPause.setEnabled(true);
                mCancel.setEnabled(true);
                mPause.setText(getResources().getString(R.string.resume));
                break;
            default:
                throw new IllegalArgumentException("Unsupported state: " + state);
        }
    }

    private static class MainThreadHandler extends Handler {
        private final WeakReference<HandlerActivity> mActivityRef;

        private MainThreadHandler(@NonNull HandlerActivity activity) {
            super(Looper.getMainLooper());
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            HandlerActivity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case BackgroundWorker.MESSAGE_UPDATE_PROGRESS:
                    activity.mProgressView.setProgress(msg.arg1);
                    break;
                case BackgroundWorker.MESSAGE_DONE:
                    activity.setState(State.OFF);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported message: " + msg.what);
            }
        }
    }
}
