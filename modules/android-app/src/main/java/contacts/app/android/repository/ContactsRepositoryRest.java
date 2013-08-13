package contacts.app.android.repository;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import contacts.app.android.R;
import contacts.app.android.model.Contact;
import contacts.app.android.rest.AuthorizationException;
import contacts.app.android.rest.NetworkException;
import contacts.app.android.rest.ReadOnlyHttpMessageConverterDelegate;
import contacts.app.android.rest.RestClient;
import contacts.util.JsonUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static java.text.MessageFormat.format;
import java.util.Collections;

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
        restClient.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(new ReadOnlyHttpMessageConverterDelegate(new ContactsHttpMessageConverter())));
    }

    @Override
    public List<Contact> findByOffice(Account account)
            throws AuthorizationException, NetworkException {
        List<Contact> returnValue = Collections.emptyList();
        String username = account.name;

        Log.d(TAG, format("Find by office for {0}.", username));

        URI uri = resolveUri(context.getString(R.string.restPathSearchContacts));
        Contact[] tmp = restClient.doGet(username, accountManager.getPassword(account), uri, Contact[].class);
        if (tmp != null) {
            returnValue = Arrays.asList(tmp);
        }
        return returnValue;
    }

    private URI resolveUri(String path) throws NetworkException {
        try {
            return new URI(context.getString(R.string.restScheme),
                    context.getString(R.string.restAuthority), path, null, null);
        } catch (URISyntaxException exception) {
            throw new NetworkException("Invalid URI.", exception);
        }
    }

    private static class ContactsHttpMessageConverter extends MappingJacksonHttpMessageConverter {
        private ContactsHttpMessageConverter() {
            super();
            setObjectMapper(JsonUtils.createObjectMapper());
        }
    }
}
