package io.fiap.fastfood.driven.core.exception;

/**
 * This exception represents a business exception usually converted into
 * an HTTP Status 409 Conflict.
 */
public class DuplicatedKeyException extends RuntimeException {

    /**
     * <pre>Constructs a {@link DuplicatedKeyException} with default message</pre>
     * <pre><b>"Conflict"</b>.</pre>
     */
    public DuplicatedKeyException() {
        this("Conflict");
    }

    /**
     * Constructs a {@link DuplicatedKeyException}.
     *
     * @param message the detail message.
     */
    public DuplicatedKeyException(final String message) {
        super(message);
    }


    /**
     * Constructs a {@link DuplicatedKeyException}.
     *
     * @param cause the cause.
     */
    public DuplicatedKeyException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@link DuplicatedKeyException}.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public DuplicatedKeyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
