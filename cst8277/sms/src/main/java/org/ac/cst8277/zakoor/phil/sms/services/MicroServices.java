package org.ac.cst8277.zakoor.phil.sms.services;

import org.ac.cst8277.zakoor.phil.sms.dtos.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.UUID;

@Service
public class
MicroServices implements MicroService {
    @Value("${sms.host}")
    private String uriSmsHost;

    @Value("${sms.port}")
    private String uriSmsPort;

    @Value("${ums.host}")
    private String uriUmsHost;

    @Value("${ums.port}")
    private String uriUmsPort;

    public Mono<Object> retrieveSmsData(String uri, UUID token) {
        return getObjectMono(uri, uriSmsHost, uriSmsPort, token);
    }

    public Mono<Object> retrieveUmsData(String uri, UUID token) {
        return getObjectMono(uri, uriUmsHost, uriUmsPort, token);
    }

    static Mono<Object> getObjectMono(String uri, String uriHost, String uriPort, UUID token) {
        WebClient client = WebClient.builder().baseUrl(uriHost + ":" + uriPort)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE, Constants.TOKEN)
                .build();

        Mono<Object> response = client.method(HttpMethod.GET).uri(uri).accept(MediaType.APPLICATION_JSON)
                .acceptCharset(Charset.forName("UTF-8")).header(Constants.TOKEN,token.toString()).retrieve().bodyToMono(Object.class);

        return response;
    }
}
