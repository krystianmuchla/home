package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.db.changelog.ChangelogService;
import com.github.krystianmuchla.home.drive.DriveApiController;
import com.github.krystianmuchla.home.error.AppErrorHandler;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.id.controller.*;
import com.github.krystianmuchla.home.note.NoteApiController;
import com.github.krystianmuchla.home.note.grave.NoteGraveCleaner;
import com.github.krystianmuchla.home.note.sync.NoteSyncApiController;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class App {
    public static void main(final String... args) {
        ChangelogService.update();
        startHttpServer();
        startJobs();
    }

    private static void startHttpServer() {
        final var server = new Server();
        server.addConnector(createConnector(server));
        server.setHandler(createServletContextHandler());
        server.setErrorHandler(new AppErrorHandler());
        try {
            server.start();
        } catch (final Exception exception) {
            throw new InternalException(exception);
        }
    }

    private static Connector createConnector(final Server server) {
        final var connector = new ServerConnector(server, createHttpConnectionFactory());
        connector.setPort(AppConfig.PORT);
        return connector;
    }

    private static HttpConnectionFactory createHttpConnectionFactory() {
        final var httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        return new HttpConnectionFactory(httpConfiguration);
    }

    private static ServletContextHandler createServletContextHandler() {
        final var servletContextHandler = new ServletContextHandler();
        servletContextHandler.addServlet(DriveApiController.class, DriveApiController.PATH);
        servletContextHandler.addServlet(FaviconController.class, FaviconController.PATH);
        servletContextHandler.addServlet(FontController.class, FontController.PATH);
        servletContextHandler.addServlet(HealthApiController.class, HealthApiController.PATH);
        servletContextHandler.addServlet(InitSignUpApiController.class, InitSignUpApiController.PATH);
        servletContextHandler.addServlet(NoteApiController.class, NoteApiController.PATH);
        servletContextHandler.addServlet(NoteSyncApiController.class, NoteSyncApiController.PATH);
        servletContextHandler.addServlet(SignInApiController.class, SignInApiController.PATH);
        servletContextHandler.addServlet(SignInController.class, SignInController.PATH);
        servletContextHandler.addServlet(SignOutApiController.class, SignOutApiController.PATH);
        servletContextHandler.addServlet(SignUpApiController.class, SignUpApiController.PATH);
        servletContextHandler.addServlet(SignUpController.class, SignUpController.PATH);
        return servletContextHandler;
    }

    private static void startJobs() {
        final var noteGraveCleaner = new NoteGraveCleaner();
        new Thread(noteGraveCleaner).start();
    }
}
