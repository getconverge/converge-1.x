package dk.i2m.converge.ejb.services;

/**
 *
 * @author Allan Lykke Christensen
 */
public class MediaRepositoryIndexingException extends MediaRepositoryException {

    public MediaRepositoryIndexingException(Throwable cause) {
        super(cause);
    }

    public MediaRepositoryIndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaRepositoryIndexingException(String message) {
        super(message);
    }

    public MediaRepositoryIndexingException() {
        super();
    }
}
