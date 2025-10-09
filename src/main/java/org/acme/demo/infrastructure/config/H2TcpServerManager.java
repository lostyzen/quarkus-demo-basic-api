package org.acme.demo.infrastructure.config;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.h2.tools.Server;
import org.jboss.logging.Logger;

import java.sql.SQLException;

/**
 * Starts an H2 TCP server to allow concurrent access from DBeaver
 * Mode: TCP server pointing to the same file as Quarkus
 */
@ApplicationScoped
public class H2TcpServerManager {

    private static final Logger LOG = Logger.getLogger(H2TcpServerManager.class);
    private Server h2TcpServer;
    private static final int H2_TCP_PORT = 9092;

    void startH2TcpServer(@Observes StartupEvent event) {
        try {
            // Check if TCP server is already running
            if (isPortInUse(H2_TCP_PORT)) {
                LOG.infof("H2 TCP server already running on port %d", H2_TCP_PORT);
                return;
            }

            // Start H2 TCP server with access to quarkus-demo file
            h2TcpServer = Server.createTcpServer(
                "-tcp",
                "-tcpAllowOthers",
                "-tcpPort", String.valueOf(H2_TCP_PORT),
                "-baseDir", "./data",
                "-ifNotExists"  // Create database if it doesn't exist
            );

            h2TcpServer.start();
            LOG.infof("H2 TCP server started successfully on port %d", H2_TCP_PORT);
            LOG.info("DBeaver connection parameters:");
            LOG.info("   - Type: H2 Server");
            LOG.info("   - Host: localhost");
            LOG.infof("   - Port: %d", H2_TCP_PORT);
            LOG.info("   - Database: quarkus-demo");
            LOG.infof("   - URL: jdbc:h2:tcp://localhost:%d/quarkus-demo", H2_TCP_PORT);
            LOG.info("   - User: sa (password: empty)");

        } catch (SQLException e) {
            LOG.warnf("H2 TCP server could not be started: %s", e.getMessage());
            LOG.info("Alternative solution: Stop Quarkus to access with DBeaver in Embedded mode");
        }
    }

    void stopH2TcpServer(@Observes ShutdownEvent event) {
        if (h2TcpServer != null && h2TcpServer.isRunning(false)) {
            h2TcpServer.stop();
            LOG.info("H2 TCP server stopped");
        }
    }

    private boolean isPortInUse(int port) {
        try (java.net.Socket socket = new java.net.Socket("localhost", port)) {
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }
}
