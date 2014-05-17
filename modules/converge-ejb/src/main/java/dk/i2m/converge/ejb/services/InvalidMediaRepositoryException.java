package dk.i2m.converge.ejb.services;

/**
 *
 * @author Allan Lykke Christensen
 */
public class InvalidMediaRepositoryException extends MediaRepositoryException {

    public InvalidMediaRepositoryException(Throwable cause) {
        super(cause);
    }

    public InvalidMediaRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMediaRepositoryException(String message) {
        super(message);
    }

    public InvalidMediaRepositoryException() {
        super();
    }
}
