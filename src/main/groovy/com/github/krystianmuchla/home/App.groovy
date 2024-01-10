package com.github.krystianmuchla.home

import com.github.krystianmuchla.home.db.DbConnection
import com.github.krystianmuchla.home.mnemo.NoteController
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleaner
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleanerConfig
import com.github.krystianmuchla.home.mnemo.sync.NoteSyncController
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler

class App {
    static void main(final String... args) {
        updateDbSchema()
        startHttpServer()
        startSchedulers()
    }

    private static void updateDbSchema() {
        final dbConnection = DbConnection.create()
        final jdbcConnection = new JdbcConnection(dbConnection)
        final database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection)
        final liquibase = new Liquibase('db-changelog.sql', new ClassLoaderResourceAccessor(), database)
        liquibase.update()
    }

    private static void startHttpServer() {
        final server = new Server(AppConfig.PORT)
        final servletContextHandler = new ServletContextHandler()
        servletContextHandler.addServlet(HealthController, HealthController.PATH)
        servletContextHandler.addServlet(NoteController, NoteController.PATH)
        servletContextHandler.addServlet(NoteSyncController, NoteSyncController.PATH)
        server.setHandler(servletContextHandler)
        server.start()
    }

    private static void startSchedulers() {
        final noteGraveCleaner = new NoteGraveCleaner(
            NoteGraveCleanerConfig.ENABLED,
            NoteGraveCleanerConfig.RATE,
            NoteGraveCleanerConfig.RATE_UNIT,
            NoteGraveCleanerConfig.THRESHOLD,
            NoteGraveCleanerConfig.RATE_UNIT
        )
        new Thread(noteGraveCleaner).start()
    }
}
