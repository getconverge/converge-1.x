package dk.i2m.converge.ejb.services;

/**
 *
 * @author Allan Lykke Christensen
 */
public class MediaRepositoryException extends Exception {

    public MediaRepositoryException(Throwable cause) {
        super(cause);
    }

    public MediaRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaRepositoryException(String message) {
        super(message);
    }

    public MediaRepositoryException() {
        super();
    }
}
