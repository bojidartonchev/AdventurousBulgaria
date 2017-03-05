package com.codeground.wanderlustbulgaria.Utilities;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;


public class LifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static int mResumed;
    private static int mPaused;
    private static int mStarted;
    private static int mStopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++mStarted;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++mResumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++mPaused;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++mStopped;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public static boolean isApplicationVisible() {
        return mStarted > mStopped;
    }

    public static boolean isApplicationInForeground() {
        return mResumed > mPaused;
    }
}
