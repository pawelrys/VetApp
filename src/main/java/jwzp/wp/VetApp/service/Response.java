package jwzp.wp.VetApp.service;

import java.util.Objects;

public class Response<T> {

    private final T content;
    private final ResponseErrorMessage errorMessage;
    private final boolean succeed;

    private Response(T resource) {
        this.content = resource;
        this.errorMessage = null;
        this.succeed = true;
    }

    private Response(ResponseErrorMessage errorMessage) {
        this.content = null;
        this.errorMessage = errorMessage;
        this.succeed = false;
    }

    public static <T> Response<T> succeedResponse(T content) {
        return new Response<>(content);
    }

    public static <T> Response<T> errorResponse(ResponseErrorMessage errorMessage) {
        return new Response<>(errorMessage);
    }

    public boolean succeed() {
        return succeed;
    }

    public T get() {
        return content;
    }

    public ResponseErrorMessage getError() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response<?> response = (Response<?>) o;
        return succeed == response.succeed && Objects.equals(content, response.content) && errorMessage == response.errorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, errorMessage, succeed);
    }
}

