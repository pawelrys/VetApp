package jwzp.wp.VetApp.service;

import org.springframework.http.ResponseEntity;

public class Response<T> {

    private final T content;
    private final boolean succeed;

    private Response(T resource, boolean succeed){
        this.content = resource;
        this.succeed = succeed;
    }

    public static <T> Response<T> succeedResponse(T content){
        return new Response<T>(content, true);
    }

    public static <T> Response<T> errorResponse(T content){
        return new Response<T>(content, false);
    }

    public boolean succeed(){
        return succeed;
    }

    public T get(){
        return content;
    }

}

