package io.fiap.fastfood.driven.core.exception;

import java.util.concurrent.CompletionException;
import org.springframework.stereotype.Component;

/**
 * Convert {@link Throwable} instances into a {@link Integer} representing HTTP Status Code.
 */
@Component
public class HttpStatusExceptionConverter {

    /**
     * Convert {@link Throwable} into a {@link Integer} representing HTTP Status Code.
     *
     * @param throwable exception to convert
     *
     * @return {@link Integer}
     */
    public Integer convert(final Throwable throwable) {
        if (throwable instanceof BusinessException) return 422;
        else if (throwable instanceof NotFoundException) return 404;
        else if (throwable instanceof TechnicalException) return 500;
        else if (throwable instanceof UnavailableException) return 503;
        else if (throwable instanceof IllegalArgumentException) return 422;
        else if (throwable instanceof CompletionException) return this.convert(throwable.getCause());
        else return 500;
    }
}