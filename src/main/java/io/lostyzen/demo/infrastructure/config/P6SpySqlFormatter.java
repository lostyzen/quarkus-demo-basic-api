package io.lostyzen.demo.infrastructure.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

/**
 * Custom P6Spy formatter to display SQL queries
 * with clean indentation and real values (without '?')
 * Uses Hibernate formatter for professional indentation
 */
public class P6SpySqlFormatter implements MessageFormattingStrategy {

    private final BasicFormatterImpl hibernateFormatter = new BasicFormatterImpl();

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {

        // Ignore commits, rollbacks, etc.
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        // Filter only truly useless categories
        if ("debug".equals(category) || "info".equals(category) || "result".equals(category)
            || "resultset".equals(category)) {
            return "";
        }

        // Explicitly ignore commits/rollbacks
        String sqlLower = sql.trim().toLowerCase();
        if (sqlLower.equals("commit") || sqlLower.equals("rollback")
            || sqlLower.startsWith("set ") || sqlLower.startsWith("call ")) {
            return "";
        }

        // Use Hibernate formatter for professional indentation
        String formattedSql = hibernateFormatter.format(sql);

        return "\nHibernate: " + formattedSql + "\n";
    }
}
