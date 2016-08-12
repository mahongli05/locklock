package com.screenlock;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by mugku on 15. 5. 20..
 */
public class LockScreenUtil {

    private Context mContext = null;

    private static LockScreenUtil mLockscreenUtilInstance;

    public static LockScreenUtil getInstance(Context context) {
        if (mLockscreenUtilInstance == null) {
            if (null != context) {
                mLockscreenUtilInstance = new LockScreenUtil(context);
            }
            else {
                mLockscreenUtilInstance = new LockScreenUtil();
            }
        }
        return mLockscreenUtilInstance;
    }

    private LockScreenUtil() {
        mContext = null;
    }

    private LockScreenUtil(Context context) {
        mContext = context;
    }

    public boolean isStandardKeyguardState() {
        boolean isStandardKeyguqrd = false;
        KeyguardManager keyManager =(KeyguardManager) mContext.getSystemService(mContext.KEYGUARD_SERVICE);
        if (null != keyManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                isStandardKeyguqrd = keyManager.isKeyguardSecure();
            }
        }

        return isStandardKeyguqrd;
    }
}
