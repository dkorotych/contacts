package contacts.app.android.service.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service for synchronization of contacts.
 */
public class SyncContactsService extends Service {

    private SyncContactsAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();

        adapter = new SyncContactsAdapter(this, true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }

}
