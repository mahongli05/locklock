package com.screenlock;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.screenlock.view.ScreenLockContainer;

/**
 * Created by MHL on 2016/6/12.
 */

public class ScreenLockHelper implements ScreenContent.ScreenLockCallback {

    private static final boolean TEST_IMAGE = false;

    private LayoutInflater mInflater = null;
    private ScreenContent mLockContentView = null;
    private ScreenLockContainer mLockScreenContainer;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    private ScreenLockListener mListener;

    private static ScreenLockHelper sInstance = new ScreenLockHelper();

    public static ScreenLockHelper getInstance() {
        return sInstance;
    }

    private ScreenLockHelper() {

    }

    @Override
    public void onUnlocked() {

    }

    public interface ScreenLockListener {
        void onLockStart();
        void onLockStop();
    }

    public void setScreenLockListener(ScreenLockListener listener) {
        mListener = listener;
    }

    public void startLock(Context context) {

        if (canLockScreen(context)) {

            if (mWindowManager == null) {
                initState(context);
            }

            if (mLockContentView == null) {
                initView(context);
                attachLockScreenView();
            }

            if (mListener != null) {
                mListener.onLockStart();
            }
        }

        if (mWindowManager == null
                || mLockContentView == null
                || (mLockScreenContainer != null && mLockScreenContainer.getParent() == null)) {
            stopLock(true);
        }
    }

    private boolean canLockScreen(Context context) {

        if (!isLockScreenAble()) {
            return false;
        }

        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        int state = tm.getCallState();
        if (state != TelephonyManager.CALL_STATE_IDLE) {
            return false;
        }

        return true;
    }

    public void stopLock(boolean force) {
        detachLockScreenView();
        if (mListener != null) {
            mListener.onLockStop();
        }
    }

    private void initState(Context context) {

        if (context instanceof Activity) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    PixelFormat.TRANSPARENT);
            Activity activity = (Activity) context;
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mWindowManager = activity.getWindowManager();
        } else {
            boolean isLockEnable = LockScreenUtil.getInstance(context).isStandardKeyguardState();
            if (isLockEnable) {
                mParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            } else {
                mParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                        PixelFormat.TRANSLUCENT);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isLockEnable) {
                    mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                } else {
                    mParams.flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
                }
            } else {
                mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }

            mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

            if (null == mWindowManager) {
                mWindowManager = ((WindowManager) context.getSystemService(Service.WINDOW_SERVICE));
            }
        }
    }

    private void initView(final Context context) {

        if (null == mInflater) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        updateScrollPager(mInflater.getContext());

        if (null == mLockScreenContainer) {
            mLockScreenContainer = new ScreenLockContainer(mInflater.getContext());
            if (null == mLockContentView) {
                mLockContentView = ScreenContentFactory.createScreenContent(mInflater.getContext(), mLockScreenContainer);
                mLockContentView.setScreenLockCallback(this);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLockScreenContainer.addView(mLockContentView, params);
        }
    }

    private void updateScrollPager(Context context) {

    }

    private boolean isLockScreenAble() {
        return true;
    }

    private void attachLockScreenView() {

        if (null != mWindowManager && null != mLockScreenContainer && null != mParams) {
            mLockScreenContainer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            mWindowManager.addView(mLockScreenContainer, mParams);
            mLockContentView.onLockStart();
        }
    }

    private boolean detachLockScreenView() {
        if (null != mWindowManager && null != mLockScreenContainer) {
            mWindowManager.removeView(mLockScreenContainer);
            mLockContentView.onLockStop();
            mLockScreenContainer = null;
            mLockContentView = null;
            mWindowManager = null;
            return true;
        } else {
            return false;
        }
    }

    public void prepareScreenAds(Context context) {

    }



    private static final String[] imageUrl = {
            "http://reso2.yiihuu.com/970595-z.jpg",
            "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1207/30/c1/12613028_1343631802286_320x480.jpg",
            "http://b.zol-img.com.cn/sjbizhi/images/9/230x350/1460620503110.jpg",
            "http://img5.hao123.com/data/1_8918d4dcdd5a4fb9935980224a2eaad8_0",
            "http://1000.yllm.net/picture/upload/1001/141010102.jpg",
            "http://image.tianjimedia.com/uploadImages/2012/265/91NN77NQ759F.jpg",
            "http://down1.cnmo.com/cnmo-app/a187/shouhuijianbihua.jpg",
            "http://8.pic.pc6.com/up/2012-4/2012412113049774860.jpg",
            "http://b.zol-img.com.cn/sjbizhi/images/9/320x510/1453451150306.jpg",
            "http://img.download.pchome.net/43/ep/227848_600x450.jpg",
            "http://yllm.net/picture/upload/1001/14109887.jpg",
            "http://yllm.net/picture/upload/1005/201414722.jpg",
            "http://img.sc115.com/dm/phone/pic/1502ovagkjinzwg.jpg",
            "http://b.zol-img.com.cn/sjbizhi/images/1/320x510/1350889009297.jpg",
            "http://download.pchome.net/wallpaper/pic-8519-4.jpg",
            "http://yunosshequ.oss-cn-hangzhou.aliyuncs.com/yunosbbs/attachment/Mon_1310/47_1738495431_0a842d0f60a46be.jpg?109",
            "http://down1.cnmo.com/cnmo-app/a203/lansexingkong.jpg",
            "http://www.huanxinjie.com/uploads/allimg/160203/0GS9AO_0.jpeg",
            "http://down1.cnmo.com/cnmo-app/a211/wodeshijiezhiyounidong.jpg",
            "http://download.pchome.net/wallpaper/pic-982-8.jpg",
            "http://www.wmpic.me/wp-content/uploads/2013/10/20131015223701611.jpg",
            "http://5.1015600.com/download/pic/000/244/37d83eafa21742212b88f235d13b63a9.jpg",
            "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1407/16/c1/36371496_1405493186084_320x480.png",
            "http://img.sc115.com/dm/phone/pic/1502pv5j2bvyesz.jpg",
            "http://b.zol-img.com.cn/sjbizhi/images/5/320x510/1371116221624.jpg",
            "http://img4.duitang.com/uploads/blog/201310/09/20131009193047_j2xxU.jpeg",
            "http://img1.gamedog.cn/2011/12/10/7-111210223016.jpg",
            "http://b.zol-img.com.cn/sjbizhi/images/2/320x510/1351758295957.jpg",
            "http://i2.download.fd.pchome.net/t_320x520/g1/M00/0B/19/oYYBAFQ2YFCIA0ZyAAPKjDPME6MAAB_QQEPw-MAA8qk047.jpg",
            "http://desktop.kole8.com/handset_desktop/desk_file-11/16/43/2012/10/2012102412474211.jpg"
    };
}
