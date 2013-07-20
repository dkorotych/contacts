package contacts.app.android.repository;

public class RepositoryException extends Exception {

    private static final long serialVersionUID = 8362529010456561200L;

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
