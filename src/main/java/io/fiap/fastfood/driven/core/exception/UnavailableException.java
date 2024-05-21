package io.fiap.fastfood.driven.core.exception;

/**
 * This exception represents that application or it's dependencies aren't healthy
 * and usually is converted into a {@link java.net.HttpURLConnection#HTTP_UNAVAILABLE}.
 */
public class UnavailableException extends RuntimeException {

    /**
     * <pre>Constructs a {@link UnavailableException} with default message</pre>
     * <pre><b>"Service Unavailable."</b>.</pre>
     */
    public UnavailableException() {
        this("Service Unavailable");
    }

    /**
     * Constructs a {@link UnavailableException}.
     *
     * @param message the detail message.
     */
    public UnavailableException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@link UnavailableException}.
     *
     * @param cause the cause.
     */
    public UnavailableException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@link UnavailableException}.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public UnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
