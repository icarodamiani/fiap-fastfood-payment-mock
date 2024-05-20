package io.fiap.fastfood.driven.core.exception;

/**
 * This exception represents a business exception usually converted into
 * an HTTP Status 422 Unprocessable Entity.
 */
public class BusinessException extends RuntimeException {

    /**
     * <pre>Constructs a {@link BusinessException} with default message</pre>
     * <pre><b>"Unprocessable Entity. Something happened during processing this
     * entity. This may be interpreted usually a business error."</b>.</pre>
     */
    public BusinessException() {
        this("Unprocessable Entity\nSomething happened during processing this entity\nThis may be interpreted usually a business error");
    }

    /**
     * Constructs a {@link BusinessException}.
     *
     * @param message the detail message.
     */
    public BusinessException(final String message) {
        super(message);
    }


    /**
     * Constructs a {@link BusinessException}.
     *
     * @param cause the cause.
     */
    public BusinessException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@link BusinessException}.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public BusinessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
