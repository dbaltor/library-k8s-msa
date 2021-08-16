package book.adapter.gateway.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static java.util.stream.Collectors.*;

import org.springframework.http.HttpHeaders;
//import com.netflix.hystrix.exception.HystrixBadRequestException;

import feign.Response;
import feign.codec.ErrorDecoder;

import lombok.Getter;
import lombok.val;

public class Propagate4xxFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        val status = response.status();
        if (status / 100 == 4) {
            var body = "4xx client error";
            try {
                body = new BufferedReader(response.body().asReader(StandardCharsets.UTF_8))
                    .lines()
                    .collect(joining("\n"));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            val httpHeaders = new HttpHeaders();
            response.headers().forEach((key, values) -> httpHeaders.add("feign-" + key, String.join(",", values)));
            return new Feign4xxResponseException(status, httpHeaders, body);
        } else {
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    @Getter
    public static class Feign4xxResponseException extends RuntimeException {
    //public static class Feign4xxResponseException extends HystrixBadRequestException {
        private static final long serialVersionUID = 1L;
        private final int status;
        private final HttpHeaders headers;
        private final String body;

        public Feign4xxResponseException(int status, HttpHeaders headers, String body) {
            super("Bad request");
            this.status = status;
            this.headers = headers;
            this.body = body;
        }
    }
}

