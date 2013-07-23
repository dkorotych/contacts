package contacts.app.android.rest;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Base64;
import android.util.Log;

/**
 * Client for REST-services.
 */
public class RestClient {

    private static final String TAG = RestClient.class.getName();

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public RestClient() {
    }

    /**
     * Sends GET request.
     * 
     * @param username
     *            the username for basic authentication.
     * @param password
     *            the password fir basic authentication.
     * @param uri
     *            the target URI.
     * 
     * @return the retrieved data.
     * 
     * @throws NetworkException
     *             request could not be fulfilled.
     * @throws AuthorizationException
     *             user credentials are invalid.
     */
    public String doGet(String username, String password, URI uri)
            throws AuthorizationException, NetworkException {
        HttpGet request = new HttpGet();
        String authHeader = createAuthHeader(username, password);
        request.setHeader(AUTHORIZATION_HEADER, authHeader);
        request.setURI(uri);

        Log.d(TAG, format("Send GET request to {0}.", uri.toString()));

        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new AuthorizationException("Authorization failed.");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new NetworkException("Invalid status.");
            }

            return EntityUtils.toString(response.getEntity());
        } catch (IOException exception) {
            throw new NetworkException("Connection error.", exception);
        }
    }

    private static String createAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String base64 = new String(Base64.encode(credentials.getBytes(),
                Base64.NO_WRAP));
        return "Basic " + base64;
    }

}
