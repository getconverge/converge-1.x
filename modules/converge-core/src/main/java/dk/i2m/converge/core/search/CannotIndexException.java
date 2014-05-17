package dk.i2m.converge.core.search;

/**
 *
 * @author Allan Lykke Christensen
 */
public class CannotIndexException extends Exception {

    public CannotIndexException(Throwable cause) {
        super(cause);
    }

    public CannotIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotIndexException(String message) {
        super(message);
    }

    public CannotIndexException() {
        super();
    }
}
