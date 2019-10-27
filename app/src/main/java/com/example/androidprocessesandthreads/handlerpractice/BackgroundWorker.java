package com.example.androidprocessesandthreads.handlerpractice;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.Random;

/**
 * Делает работу в бэкграунде
 *
 * @author Evgeny Chumak
 **/
public class BackgroundWorker extends HandlerThread {
    static final int MESSAGE_UPDATE_PROGRESS = -1;
    static final int MESSAGE_DONE = 0;
    private static final int MESSAGE_START = 1;
    private static final int MESSAGE_PAUSE = 2;
    private static final int MESSAGE_RESUME = 3;
    private static final int MESSAGE_CANCEL = 4;
    private static final int MESSAGE_DO_NEXT_JOB = 5;
    private static final int JOB_MIN_TIME = 30;
    private static final int JOB_MAX_TIME = 70;

    private Handler mClient;
    private Handler mBackgroundHandler;
    private int mProgress;
    private Random mRandom = new Random(System.currentTimeMillis());

    BackgroundWorker(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        mBackgroundHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MESSAGE_START:
                        mProgress = 0;
                    case MESSAGE_DO_NEXT_JOB:
                    case MESSAGE_RESUME:
                        sendProgress(mProgress);
                        doMoreWorkOrFinish();
                        break;
                    case MESSAGE_PAUSE:
                        if (mBackgroundHandler.hasMessages(MESSAGE_DO_NEXT_JOB)) {
                            mProgress--;
                        }
                        mBackgroundHandler.removeMessages(MESSAGE_DO_NEXT_JOB);
                        break;
                    case MESSAGE_CANCEL:
                        mBackgroundHandler.removeMessages(MESSAGE_START);
                        mBackgroundHandler.removeMessages(MESSAGE_PAUSE);
                        mBackgroundHandler.removeMessages(MESSAGE_RESUME);
                        mBackgroundHandler.removeMessages(MESSAGE_DO_NEXT_JOB);
                        sendProgress(0);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported message: " + msg.what);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        mClient = null;
        return super.quit();
    }

    @Override
    public boolean quitSafely() {
        mClient = null;
        return super.quitSafely();
    }

    /**
     * Запускает асинхронную задачу
     */
    void startWork() {
        sendBackgroundCommand(MESSAGE_START);
    }

    /**
     * Приостанавливает асинхронную задачу
     */
    void pauseWork() {
        sendBackgroundCommand(MESSAGE_PAUSE);
    }

    /**
     * Продолжает асинхронную задачу
     */
    void resumeWork() {
        sendBackgroundCommand(MESSAGE_RESUME);
    }

    /**
     * Отменяет асинхронную задачу
     */
    void cancelWork() {
        sendBackgroundCommand(MESSAGE_CANCEL);
    }

    /**
     * Устанавливает {@link Handler} со стороны клиента. Этот хэндлер будет принимать сообщения от worker`a
     * и соответствующим образом обновлять UI.
     */
    void setClient(@NonNull Handler client) {
        mClient = client;
    }

    @NonNull
    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            throw new IllegalArgumentException("Handler is not ready yet");
        }
        return mBackgroundHandler;
    }

    private void sendProgress(int progress) {
        if (mClient != null) {
            Message updateProgress = mClient.obtainMessage(MESSAGE_UPDATE_PROGRESS, progress, 0);
            mClient.sendMessage(updateProgress);
        }
    }

    private void doMoreWorkOrFinish() {
        if (mProgress >= 100) {
            Message message = mBackgroundHandler.obtainMessage(MESSAGE_DONE);
            if (mClient != null) {
                mClient.sendMessage(message);
            }
        } else {
            simulateHardWork();
            Message message = mBackgroundHandler.obtainMessage(MESSAGE_DO_NEXT_JOB, ++mProgress, 0);
            mBackgroundHandler.sendMessage(message);
        }
    }

    private void simulateHardWork() {
        int jobTime = mRandom.nextInt(JOB_MAX_TIME - JOB_MIN_TIME) + JOB_MIN_TIME;
        try {
            Thread.sleep(jobTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendBackgroundCommand(int command) {
        Handler handler = getBackgroundHandler();
        Message message = handler.obtainMessage(command);
        handler.sendMessage(message);
    }

}
