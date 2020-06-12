//package com.icbc.axb.config;
//
//import okhttp3.*;
//import okhttp3.logging.HttpLoggingInterceptor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.ClientHttpRequestFactory;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
//import org.springframework.web.client.DefaultResponseErrorHandler;
//import org.springframework.web.client.ResponseErrorHandler;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class RestClientConfig {
//
//    /** 读取超时时间 */
//    private static final long READ_TIMEOUT = 60;
//    /** 写入超时时间 */
//    private static final long WRITE_TIMEOUT = 60;
//    /** 连接超时时间 */
//    private static final long CONNECT_TIMEOUT = 30;
//    /** 最大连接数 */
//    private static final int MAX_CONNECTION_COUNT = 50;
//    /** 长连接期间周期 */
//    private static final long KEEP_ALIVE_DURATION = 5L;
//    private final Logger logger = LoggerFactory.getLogger(RestClientConfig.class);
//
//    private static final String CONTENT_TYPE = "Content-Type";
//
//    @Bean
//    public HttpLoggingInterceptor providerHttpLoggingInterceptor() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        return interceptor;
//    }
//
//    @Bean
//    public ConnectionPool providerConnectionPool() {
//        return new ConnectionPool(MAX_CONNECTION_COUNT, KEEP_ALIVE_DURATION, TimeUnit.MINUTES);
//    }
//
//    @Bean
//    public ResponseErrorHandler providerResponseErrorHandler() {
//        return new MyErrorHandler();
//    }
//
//    @Bean
//    @Autowired
//    public OkHttpClient providerOkHttpClient(
//            HttpLoggingInterceptor interceptor,
//            ConnectionPool connectionPool) {
//
//        OkHttpClient.Builder builder =
//                new OkHttpClient.Builder()
//                        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
//                        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
//                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
//                        .addInterceptor(new RequestInterceptor())
//                        .addInterceptor(interceptor)
//                        .connectionPool(connectionPool)
//                        .retryOnConnectionFailure(true);
//        return builder.build();
//    }
//
//    @Bean
//    @Autowired
//    public ClientHttpRequestFactory providerRequestFactory(OkHttpClient client) {
//        return new OkHttp3ClientHttpRequestFactory(client);
//    }
//
//    @Bean("restTemplate")
//    @Autowired
//    public RestTemplate providerRestTemplate(
//            ClientHttpRequestFactory clientHttpRequestFactory, ResponseErrorHandler errorHandler) {
//
//        RestTemplate restTemplate = new RestTemplate();
//        // 使用okhttp3
//        restTemplate.setRequestFactory(clientHttpRequestFactory);
//        // 异常处理
//        restTemplate.setErrorHandler(errorHandler);
//        return restTemplate;
//    }
//
//    private static class MyErrorHandler extends DefaultResponseErrorHandler {
//
//        private final Logger logger = LoggerFactory.getLogger(RestClientConfig.class);
//
//        @Override
//        protected void handleError(ClientHttpResponse response, HttpStatus statusCode) {
//            try {
//                super.handleError(response, statusCode);
//            } catch (Exception e) {
//                // e.printStackTrace();
//                logger.error("Rest api error:{}", e.getMessage());
//            }
//        }
//    }
//
//    private static class RequestInterceptor implements Interceptor {
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//
//            Request request = chain.request();
//            Headers originHeaders = request.headers();
//            String contentType = originHeaders.get("Content-Type");
//            if (Objects.nonNull(contentType)
//                    && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
//                Headers.Builder builder = originHeaders.newBuilder();
//                builder.removeAll(CONTENT_TYPE);
//                builder.add(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//                Headers headers = builder.build();
//                Request.Builder requestBuilder = request.newBuilder();
//                requestBuilder.headers(headers);
//                request = requestBuilder.build();
//            }
//            return chain.proceed(request);
//        }
//    }
//}
