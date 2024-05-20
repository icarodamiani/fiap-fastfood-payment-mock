package io.fiap.fastfood.driven.core.exception;

/**
 * This exception represents a broken message and usually is converted into
 * a {@link java.net.HttpURLConnection#HTTP_BAD_REQUEST}.
 */
public class BadRequestException extends RuntimeException {

    /**
     * <pre>Constructs a {@link BadRequestException} with default message</pre>
     * <pre><b>"BadRequest"</b>.</pre>
     */
    public BadRequestException() {
        this("BadRequest");
    }

    /**
     * Constructs a {@link BadRequestException}.
     *
     * @param message the detail message.
     */
    public BadRequestException(final String message) {
        super(message);
    }


    /**
     * Constructs a {@link BadRequestException}.
     *
     * @param cause the cause.
     */
    public BadRequestException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@link BadRequestException}.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
