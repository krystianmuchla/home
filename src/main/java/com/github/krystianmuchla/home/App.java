package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.db.DbConnection;
import com.github.krystianmuchla.home.mnemo.NoteController;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleaner;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleanerConfig;
import com.github.krystianmuchla.home.mnemo.sync.NoteSyncController;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class App {
    public static void main(final String... args) throws Exception {
        updateDbSchema();
        startHttpServer();
        startSchedulers();
    }

    private static void updateDbSchema() throws LiquibaseException {
        final var dbConnection = DbConnection.create();
        final var jdbcConnection = new JdbcConnection(dbConnection);
        final var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
        final var liquibase = new Liquibase("db-changelog.sql", new ClassLoaderResourceAccessor(), database);
        liquibase.update();
    }

    private static void startHttpServer() throws Exception {
        final var server = new Server(AppConfig.PORT);
        final var servletContextHandler = new ServletContextHandler();
        servletContextHandler.addServlet(HealthController.class, HealthController.PATH);
        servletContextHandler.addServlet(NoteController.class, NoteController.PATH);
        servletContextHandler.addServlet(NoteSyncController.class, NoteSyncController.PATH);
        server.setHandler(servletContextHandler);
        server.start();
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
