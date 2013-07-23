package contacts.app.android.service.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for adding of new user accounts.
 */
public class AddAccountService extends Service {

    private static final String TAG = AddAccountService.class.getName();

    private AccountAutheticator autheticator;

    @Override
    public void onCreate() {
        super.onCreate();

        autheticator = new AccountAutheticator(this);

        Log.d(TAG, "Service created.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return autheticator.getIBinder();
    }

}
