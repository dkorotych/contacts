package contacts.app.android.service.sync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import contacts.app.android.model.Contact;
import contacts.app.android.repository.ContactsRepository;
import contacts.app.android.repository.ContactsRepositoryRest;
import contacts.app.android.repository.RepositoryException;

/**
 * Synchronizes contacts.
 */
public class SyncContactsAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "Sync Contacts";

    private ContactsRepository contactsRepository;

    public SyncContactsAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        contactsRepository = new ContactsRepositoryRest(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        try {
            List<Contact> contacts = contactsRepository.findByLocation(account);
            addContacts(account, contacts);
        } catch (RepositoryException exception) {
            Log.e(TAG, "Repository is not accessible.", exception);
        }
    }

    private void addContacts(Account account, List<Contact> addedContacts) {
        Set<String> knownContacts = getKnownContacts(account);

        for (Contact addedContact : addedContacts) {
            String userName = addedContact.getUserName();
            if (knownContacts.contains(userName)) {
                Log.d(TAG, "Contact for user " + userName + " already exists.");
                continue;
            }

            addContact(account, addedContact);
        }
    }

    private void addContact(Account account, Contact addedContact) {
        Log.d(TAG, "Add contact for " + addedContact.getUserName());

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(createContact(account, addedContact.getUserName()));
        ops.add(addField(
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                addedContact.getDisplayName()));
        ops.add(addField(
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                addedContact.getMail()));
        ops.add(addField(
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                addedContact.getFormattedPhone()));
        ops.add(addField(
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Organization.COMPANY,
                addedContact.getLocation()));

        try {
            getContext().getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, ops);
        } catch (Exception exception) {
            Log.e(TAG, "Contact was not added.", exception);
        }
    }

    private Set<String> getKnownContacts(Account account) {
        Set<String> knownContacts = new HashSet<String>();

        Uri contactsUri = RawContacts.CONTENT_URI.buildUpon()
                .appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name)
                .appendQueryParameter(RawContacts.ACCOUNT_TYPE, account.type)
                .build();

        Cursor cursor = getContext().getContentResolver().query(contactsUri,
                new String[] { RawContacts.SYNC1 }, null, null, null);

        if (cursor.getCount() == 0) {
            return Collections.emptySet();
        }

        for (int i = 0; i < cursor.getCount(); ++i) {
            cursor.moveToNext();
            knownContacts.add(cursor.getString(0));
        }

        cursor.close();

        Log.d(TAG, "Found " + knownContacts.size() + " known contacts.");
        return knownContacts;
    }

    private static ContentProviderOperation createContact(Account account,
            String id) {
        return ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_NAME, account.name)
                .withValue(RawContacts.ACCOUNT_TYPE, account.type)
                .withValue(RawContacts.SYNC1, id).build();
    }

    private static ContentProviderOperation addField(String type, String key,
            String value) {
        return ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.MIMETYPE, type)
                .withValue(key, value)
                .withValueBackReference(
                        ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID,
                        0).build();
    }

}
