package ma.com.locklock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.screenlock.ScreenLockService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, ScreenLockService.class);
        startService(intent);
    }
}
