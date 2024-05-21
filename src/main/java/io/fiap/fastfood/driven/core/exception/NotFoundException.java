package io.fiap.fastfood.driven.core.exception;

/**
 * This exception represents something that cannot be found and usually is converted into
 * a {@link java.net.HttpURLConnection#HTTP_NOT_FOUND}.
 */
public class NotFoundException extends RuntimeException {

    /**
     * <pre>Constructs a {@link NotFoundException} with default message</pre>
     * <pre><b>"Not Found"</b>.</pre>
     */
    public NotFoundException() {
        this("Not Found");
    }

    /**
     * Constructs a {@link NotFoundException}.
     *
     * @param message the detail message.
     */
    public NotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@link NotFoundException}.
     *
     * @param cause the cause.
     */
    public NotFoundException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@link NotFoundException}.
     *
     * @param  message the detail message.
     * @param cause the cause.
     */
    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
