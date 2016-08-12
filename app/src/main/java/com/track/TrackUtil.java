package com.track;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class TrackUtil {
    static GoogleAnalytics mAnalytics;
    static Tracker mTracker;

    public static void init(Context context) {
        mAnalytics = GoogleAnalytics.getInstance(context);
        mTracker = mAnalytics.newTracker("UA-81281355-1");
        mTracker.enableExceptionReporting(true);
        mTracker.enableAutoActivityTracking(true);
    }

    public static void trackMenuHome() {
        trackMenuTap("home");
    }

    public static void trackMenuIncomeDetail() {
        trackMenuTap("incomedetail");
    }

    public static void trackMenuReward() {
        trackMenuTap("reward");
    }

    public static void trackMenuInvite() {
        trackMenuTap("invite");
    }

    public static void trackMenuSettings() {
        trackMenuTap("settings");
    }

    public static void trackMenuAboutus() {
        trackMenuTap("aboutus");
    }

    public static void trackMenuCancle() {
        trackMenuTap("cancle");
    }

    public static void trackMenuTap(String menuName) {
        trackHomeMenuTap(menuName);
    }

    public static void trackBalanceTap() {
        trackHomeAction("balance_tap");
    }

    public static void trackRewardTap() {
        trackHomeAction("reward_tap");
    }

    public static void trackInviteTap() {
        trackHomeAction("invite_tap");
    }

    public static void trackNewBieFbTap() {
        trackHomeNewbieTap("fb");
    }

    public static void trackNewBieFirstTap() {
        trackHomeNewbieTap("first");
    }

    public static void trackNewBieInviteTap() {
        trackHomeNewbieTap("invite");
    }

    public static void trackNewBieProfileTap() {
        trackHomeNewbieTap("profile");
    }

    public static void trackNewBieCloseTap() {
        trackHomeNewbieTap("close");
    }

    public static void trackMessageTap() {
        trackHomeAction("message_tap");
    }

    public static void trackTaskCpi() {
        trackHomeTaskTap("cpi");
    }

    public static void trackTaskCpa() {
        trackHomeTaskTap("cpa");
    }

    public static void trackUnlockScreenLeft() {
        trackUnlock("left");
    }

    public static void trackUnlockScreenRight() {
        trackUnlock("right");
    }

    /*
     type:notification/allow_lock/disable_system_lock
     status:on/off
     */
    public static void trackSettings(String type, boolean isOn) {
        String status = isOn ? "On" : "Off";
        mTracker.send(new HitBuilders.EventBuilder().setCategory("settings").setAction(type).setLabel(status).setValue(1).build());
    }

    public static void trackHomeMenuTap(String labelName) {
        mTracker.send(new HitBuilders.EventBuilder().setCategory("homepage").setAction("menu_tap").setLabel(labelName).setValue(1).build());
    }

    public static void trackHomeAction(String actionName) {
        mTracker.send(new HitBuilders.EventBuilder().setCategory("homepage").setAction(actionName).setValue(1).build());
    }
    public static void trackHomeTaskTap(String typeName) {
        mTracker.send(new HitBuilders.EventBuilder().setCategory("homepage").setAction("task_tap").setLabel(typeName).setValue(1).build());
    }

    public static void trackHomeNewbieTap(String labelName) {
        mTracker.send(new HitBuilders.EventBuilder().setCategory("homepage").setAction("newbie_tap").setLabel(labelName).setValue(1).build());
    }

    public static void trackUnlock(String actionName) {
        mTracker.send(new HitBuilders.EventBuilder().setCategory("unlock_screen").setAction(actionName).setValue(1).build());
    }
}
