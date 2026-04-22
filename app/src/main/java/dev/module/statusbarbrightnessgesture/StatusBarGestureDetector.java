package dev.module.statusbarbrightnessgesture;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import de.robv.android.xposed.XposedBridge;

public class StatusBarGestureDetector {
    private static final String TAG = "StatusBarGestureDetector";

    public enum GestureType {
        SINGLE_TAP,
        DOUBLE_TAP,
        LONG_TAP
    }

    public interface OnGestureListener {
        void onGesture(View view, MotionEvent ev, GestureType type);
    }

    private final GestureDetector mDetector;
    private final OnGestureListener mListener;
    private View mCurrentView;

    public StatusBarGestureDetector(Context context, OnGestureListener listener) {
        mListener = listener;
        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mListener != null && mCurrentView != null) {
                    mListener.onGesture(mCurrentView, e, GestureType.SINGLE_TAP);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mListener != null && mCurrentView != null) {
                    mListener.onGesture(mCurrentView, e, GestureType.DOUBLE_TAP);
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mListener != null && mCurrentView != null) {
                    mListener.onGesture(mCurrentView, e, GestureType.LONG_TAP);
                }
            }
        }, new Handler(Looper.getMainLooper()));
    }

    public void onTouchEvent(View view, MotionEvent ev) {
        mCurrentView = view;
        mDetector.onTouchEvent(ev);
    }
}
