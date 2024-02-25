package com.github.krystianmuchla.home;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.github.krystianmuchla.home.db.ConnectionManager;
import com.github.krystianmuchla.home.id.SignInController;
import com.github.krystianmuchla.home.id.SignOutController;
import com.github.krystianmuchla.home.id.SignUpController;
import com.github.krystianmuchla.home.mnemo.NoteController;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleaner;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleanerConfig;
import com.github.krystianmuchla.home.mnemo.sync.NoteSyncController;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class App {
    public static void main(final String... args) throws Exception {
        updateDbSchema();
        startHttpServer();
        startSchedulers();
    }

    private static void updateDbSchema() throws LiquibaseException {
        final var connection = ConnectionManager.getConnection();
        final var jdbcConnection = new JdbcConnection(connection);
        final var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
        final var liquibase = new Liquibase("db-changelog.sql", new ClassLoaderResourceAccessor(), database);
        liquibase.update();
    }

    private static void startHttpServer() throws Exception {
        final var server = new Server();
        server.addConnector(createConnector(server));
        server.setHandler(createServletContextHandler());
        server.start();
    }

    private static Connector createConnector(final Server server) {
        final var connector = new ServerConnector(server, createHttpConnectionFactory());
        connector.setPort(AppConfig.PORT);
        return connector;
    }

    private static HttpConnectionFactory createHttpConnectionFactory() {
        final HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        return new HttpConnectionFactory(httpConfiguration);
    }

    private static ServletContextHandler createServletContextHandler() {
        final var servletContextHandler = new ServletContextHandler();
        servletContextHandler.addServlet(HealthController.class, HealthController.PATH);
        servletContextHandler.addServlet(NoteController.class, NoteController.PATH);
        servletContextHandler.addServlet(NoteSyncController.class, NoteSyncController.PATH);
        servletContextHandler.addServlet(SignInController.class, SignInController.PATH);
        servletContextHandler.addServlet(SignOutController.class, SignOutController.PATH);
        servletContextHandler.addServlet(SignUpController.class, SignUpController.PATH);
        return servletContextHandler;
    }

    private static void startSchedulers() {
        final var noteGraveCleaner = new NoteGraveCleaner(
            NoteGraveCleanerConfig.ENABLED,
            NoteGraveCleanerConfig.RATE,
            NoteGraveCleanerConfig.RATE_UNIT,
            NoteGraveCleanerConfig.THRESHOLD,
            NoteGraveCleanerConfig.RATE_UNIT
        );
        new Thread(noteGraveCleaner).start();
    }
}
