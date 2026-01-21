package eg.alrawi.alrawi_award.audit;

import eg.alrawi.alrawi_award.entity.AuditLog;
import eg.alrawi.alrawi_award.repository.AuditLogRepository;
import eg.alrawi.alrawi_award.utils.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Order(4)
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditFilter extends OncePerRequestFilter {

    private final AuditLogRepository repository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
            if(!request.getRequestURI().contains("/api/v1/auth/hello"))
                saveAudit(request, response, start,request.getAttribute("txId").toString());
        }catch (Exception e){
            log.info("Exception in audit filter ",e);
        }
    }



 private void saveAudit(HttpServletRequest request, HttpServletResponse response, long start, String txId) {
        try {
            AuditLog log = new AuditLog();
            log.setHttpMethod(request.getMethod());
            log.setUri(request.getRequestURI());
            log.setUsername(SecurityUtil.getUsername());
            log.setRoles(SecurityUtil.getRoles());
            log.setIpAddress(request.getLocalAddr() +":"+ request.getLocalPort());
            log.setStatusCode(response.getStatus());
            log.setSuccess(response.getStatus() < 400);
            log.setTransactionId(txId);
            log.setResponseTime(System.currentTimeMillis() - start);

            repository.save(log);

        } catch (Exception ignored) {

        }
    }


}