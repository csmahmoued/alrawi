package eg.alrawi.alrawi_award.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.dgc.VMID;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private static final int MAX_LOG_SIZE=10000;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(response);
        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);

        String transactionId = new VMID().toString();
        String operationName = getOperationName(request);
        Thread.currentThread().setName("transactionId[" + transactionId + "] operationName [" + operationName + "]");
        MDC.put("txId", transactionId);
        request.setAttribute("txId", transactionId);

        if(!request.getRequestURI().contains("/api/v1/auth/hello"))
            log.info("Start url {}",request.getRequestURI());


        try {
            filterChain.doFilter(request, resp);
        } finally {

            logRequest(req);

            byte[] body = resp.getContentAsByteArray();
            log.info("Response: {}", new String(body, resp.getCharacterEncoding()));
            resp.copyBodyToResponse();
            MDC.clear();
        }
    }

    private static String getOperationName(HttpServletRequest request) {
        ContentCachingRequestWrapper req = wrapRequest(request);
        String methodName = req.getMethod();
        String requestUrl = request.getRequestURI();
        return methodName + requestUrl;
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }





    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().startsWith("multipart/");
    }

    private void logRequest(ContentCachingRequestWrapper request) {

        log.info("request {} {}", request.getMethod(), request.getRequestURI());
        if (isMultipart(request)) {
            logMultipartFormData(request);
            logMultipartFileMetadata(request);
        }
         else {
            logJsonBody(request);
        }
    }


    private void logMultipartFormData(HttpServletRequest request) {
        Map<String, String[]> multiParams = request.getParameterMap();
        if (multiParams.isEmpty()) return;

        Map<String, Object> fields = new HashMap<>();
        multiParams.forEach((k, v) -> fields.put(k, v.length == 1 ? v[0] : v));

        log.info("Multipart Form-Data (FIELDS): {}", fields);
    }

    private  void logMultipartFileMetadata(HttpServletRequest request) {
        if (!(request instanceof MultipartHttpServletRequest multipart)) return;

        multipart.getFileMap().forEach((name, file) -> {
            log.info(" File: field={} filename={} size={}KB contentType={}", name, file.getOriginalFilename(), file.getSize() / 1024, file.getContentType());
        });
    }

    private void logJsonBody(ContentCachingRequestWrapper request) {

        if (isBinary(request.getContentType())) return;

        byte[] body = request.getContentAsByteArray();
        if (body.length == 0) return;

        String payload = new String(body, StandardCharsets.UTF_8);

        if (payload.length() > MAX_LOG_SIZE) {
            payload = payload.substring(0, MAX_LOG_SIZE) + "...[TRUNCATED]";
        }

        log.info(" Request Body: {}", payload);
    }


    private boolean isBinary(String contentType) {
        if (contentType == null) return false;

        return contentType.startsWith("application/octet-stream")
                || contentType.startsWith("image/")
                || contentType.startsWith("video/")
                || contentType.startsWith("audio/");
    }
}