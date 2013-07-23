package contacts.app.android.repository;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import contacts.app.android.R;
import contacts.app.android.model.Contact;
import contacts.app.android.rest.AuthorizationException;
import contacts.app.android.rest.NetworkException;
import contacts.app.android.rest.RestClient;

/**
 * Remote repository that provides access through REST.
 */
public class ContactsRepositoryRest implements ContactsRepository {

    private static final String TAG = ContactsRepositoryRest.class.getName();

    private Context context;
    private AccountManager accountManager;

    private RestClient restClient;

    public ContactsRepositoryRest(Context context) {
        this.context = context;
        this.accountManager = AccountManager.get(context);

        this.restClient = new RestClient();
    }

    public List<Contact> findByOffice(Account account)
            throws AuthorizationException, NetworkException {
        String username = account.name;
        String relativePath = context.getString(R.string.restSearchContacts);

        Log.d(TAG, format("Find by office for {0}.", username));

        String content = restClient.doGet(username,
                accountManager.getPassword(account), resolveUri(relativePath));

        try {
            return parseContacts(content);
        } catch (JSONException exception) {
            throw new NetworkException("Invalid data format.", exception);
        }
    }

    /**
     * Parses JSON and creates a list of contacts.
     */
    private List<Contact> parseContacts(String content) throws JSONException {
        JSONArray jsonContacts = new JSONArray(content);
        List<Contact> contacts = new ArrayList<Contact>();
        for (int i = 0; i < jsonContacts.length(); ++i) {
            JSONObject jsonContact = jsonContacts.getJSONObject(i);
            contacts.add(Contact.fromJson(jsonContact));
        }
        return contacts;
    }

    /**
     * Resolves URI of REST service.
     * 
     * @param relativePath
     *            the relative path for REST-service.
     * 
     * @return the URI to access REST-service.
     * 
     * @throws RepositoryException
     *             URI could not be resolved.
     */
    private URI resolveUri(String relativePath) throws NetworkException {
        try {
            URI base = new URI(context.getString(R.string.restBase));
            return base.resolve(relativePath);
        } catch (URISyntaxException exception) {
            throw new NetworkException("Invalid URI.", exception);
        }
    }

}
