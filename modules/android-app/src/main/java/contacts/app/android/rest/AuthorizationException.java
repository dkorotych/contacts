package contacts.app.android.rest;

/**
 * Thrown if user is not authorized to use repository.
 */
public class AuthorizationException extends Exception {

    private static final long serialVersionUID = -1619686001692528827L;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

}
