package eg.alrawi.alrawi_award.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public final class CorrelationIdUtil {

    public static final String TX_ID = "txId";

    private CorrelationIdUtil() {}

    public static String getOrCreate(HttpServletRequest request) {
        String tx = request.getHeader("Transaction-Id");
        return (tx != null && !tx.isBlank())
                ? tx
                : UUID.randomUUID().toString();
    }
}
