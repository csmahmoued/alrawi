package eg.alrawi.alrawi_award.audit;

import eg.alrawi.alrawi_award.entity.AuditLog;
import eg.alrawi.alrawi_award.repository.AuditLogRepository;
import eg.alrawi.alrawi_award.utils.CorrelationIdUtil;
import eg.alrawi.alrawi_award.utils.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuditFilter extends OncePerRequestFilter {

    private final AuditLogRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String txId = CorrelationIdUtil.getOrCreate(request);

        MDC.put(CorrelationIdUtil.TX_ID, txId);
        response.setHeader("Transaction-Id", txId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            saveAudit(request, response, start,txId);
        }
    }




    private void saveAudit(HttpServletRequest request, HttpServletResponse response, long start, String txId) {
        try {
            AuditLog log = new AuditLog();
            log.setHttpMethod(request.getMethod());
            log.setUri(request.getRequestURI());
            log.setUsername(SecurityUtil.getUsername());
            log.setRoles(SecurityUtil.getRoles());
            log.setIpAddress(request.getRemoteAddr());
            log.setStatusCode(response.getStatus());
            log.setSuccess(response.getStatus() < 400);
            log.setTransactionId(txId);
            log.setResponseTime(System.currentTimeMillis() - start);

            repository.save(log);

        } catch (Exception ignored) {

        }
    }


}