package contacts.app.android.repository;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import contacts.app.android.R;
import contacts.app.android.model.Contact;

/**
 * Remote repository that provides access through REST.
 */
public class ContactsRepositoryRest implements ContactsRepository {

    private static final String TAG = ContactsRepositoryRest.class.getName();

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private Context context;

    public ContactsRepositoryRest(Context context) {
        this.context = context;
    }

    public List<Contact> findByLocation(Account account)
            throws RepositoryException {
        String searchPath = context.getString(R.string.restSearchContacts);
        String data = getData(account, searchPath);

        try {
            return parseContacts(data);
        } catch (JSONException exception) {
            throw new RepositoryException("Data is invalid.", exception);
        }
    }

    private List<Contact> parseContacts(String data) throws JSONException {
        JSONArray jsonContacts = new JSONArray(data);
        List<Contact> contacts = new ArrayList<Contact>();
        for (int i = 0; i < jsonContacts.length(); ++i) {
            JSONObject jsonContact = jsonContacts.getJSONObject(i);
            contacts.add(Contact.fromJson(jsonContact));
        }
        return contacts;
    }

    private String getData(Account account, String relativePath)
            throws RepositoryException {
        HttpGet request = new HttpGet();
        String authHeader = createAuthHeader(account);
        request.setHeader(AUTHORIZATION_HEADER, authHeader);
        URI uri = resolveUri(relativePath);
        request.setURI(uri);

        Log.d(TAG, format("Get data from {0}.", uri.toString()));

        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception exception) {
            Log.e(TAG, "Repository not accessible.");
            throw new RepositoryException("Data not accessible.", exception);
        }
    }

    private URI resolveUri(String relativePath) throws RepositoryException {
        try {
            URI base = new URI(context.getString(R.string.restBase));
            return base.resolve(relativePath);
        } catch (URISyntaxException exception) {
            throw new RepositoryException("URI is invalid.", exception);
        }
    }

    private String createAuthHeader(Account account) {
        AccountManager manager = AccountManager.get(context);
        String username = account.name;
        String password = manager.getPassword(account);

        String credentials = username + ":" + password;
        String base64 = new String(Base64.encode(credentials.getBytes(),
                Base64.NO_WRAP));
        return "Basic " + base64;
    }

}
