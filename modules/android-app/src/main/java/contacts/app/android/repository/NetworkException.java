package contacts.app.android.repository;

/**
 * Thrown if data could not be received from repository.
 */
public class NetworkException extends RepositoryException {

    private static final long serialVersionUID = -2211631091480965598L;

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

}
