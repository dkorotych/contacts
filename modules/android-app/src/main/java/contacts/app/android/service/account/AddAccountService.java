package contacts.app.android.service.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service for adding of new user accounts.
 */
public class AddAccountService extends Service {

    private AccountAutheticator autheticator;

    @Override
    public void onCreate() {
        super.onCreate();

        autheticator = new AccountAutheticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return autheticator.getIBinder();
    }

}
