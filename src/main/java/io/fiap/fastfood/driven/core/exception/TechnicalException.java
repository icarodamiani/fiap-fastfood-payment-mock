package io.fiap.fastfood.driven.core.exception;

/**
 * This exception represents a fatal error that state cannot be restored easily
 * and usually is converted into a {@link java.net.HttpURLConnection#HTTP_INTERNAL_ERROR}.
 */
public class TechnicalException extends RuntimeException {

    /**
     * <pre>Constructs a {@link TechnicalException} with default message</pre>
     * <pre><b>"Internal Server Error."</b>.</pre>
     */
    public TechnicalException() {
        this("Internal Server Error");
    }

    /**
     * Constructs a {@link TechnicalException}.
     *
     * @param message the detail message.
     */
    public TechnicalException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@link TechnicalException}.
     *
     * @param cause the cause.
     */
    public TechnicalException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@link TechnicalException}.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public TechnicalException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
