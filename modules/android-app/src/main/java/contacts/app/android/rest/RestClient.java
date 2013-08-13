package contacts.app.android.rest;

import contacts.util.JsonUtils;
import org.apache.http.HttpStatus;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Client for REST-services.
 */
public class RestClient {
    private List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

    public RestClient() {
    }

    /**
     * Set the message body converters to use. These converters are used to convert from and to HTTP requests and
     * responses.
     */
    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.messageConverters = messageConverters;
    }

    /**
     * Sends GET request.
     *
     * @param username the name of user for basic authentication.
     * @param password the password fir basic authentication.
     * @param uri      the target URI.
     * @return the retrieved data.
     * @throws NetworkException       request could not be fulfilled.
     * @throws AuthorizationException user credentials are invalid.
     */
    public <Type> Type doGet(String username, String password, URI uri, Class<Type> returnValueClass)
            throws AuthorizationException, NetworkException {
        Type returnValue = null;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Collections.singletonList(JsonUtils.APPLICATION_JSON_UTF8));
        requestHeaders.setAcceptEncoding(ContentCodingType.GZIP);
        requestHeaders.setAuthorization(new HttpBasicAuthentication(username, password));
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate(true);
        restTemplate.getMessageConverters().addAll(messageConverters);
        try {
            ResponseEntity<Type> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, returnValueClass);
            int statusCode = response.getStatusCode().value();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new AuthorizationException("Invalid credentials.");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new NetworkException("Invalid status.");
            }
            if (response.hasBody()) {
                returnValue = returnValueClass.cast(response.getBody());
            }
        } catch (RestClientException exception) {
            throw new NetworkException("Connection error.", exception);
        }
        return returnValue;
    }
}
