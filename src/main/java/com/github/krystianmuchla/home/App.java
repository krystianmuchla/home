package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.db.changelog.ChangelogService;
import com.github.krystianmuchla.home.error.AppErrorHandler;
import com.github.krystianmuchla.home.id.InitSignUpController;
import com.github.krystianmuchla.home.id.SignInController;
import com.github.krystianmuchla.home.id.SignOutController;
import com.github.krystianmuchla.home.id.SignUpController;
import com.github.krystianmuchla.home.mnemo.NoteController;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleaner;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveCleanerConfig;
import com.github.krystianmuchla.home.mnemo.sync.NoteSyncController;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class App {
    public static void main(final String... args) throws Exception {
        ChangelogService.update();
        startHttpServer();
        startJobs();
    }

    private static void startHttpServer() throws Exception {
        final var server = new Server();
        server.addConnector(createConnector(server));
        server.setHandler(createServletContextHandler());
        server.setErrorHandler(new AppErrorHandler());
        server.start();
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
        servletContextHandler.addServlet(HealthController.class, HealthController.PATH);
        servletContextHandler.addServlet(InitSignUpController.class, InitSignUpController.PATH);
        servletContextHandler.addServlet(NoteController.class, NoteController.PATH);
        servletContextHandler.addServlet(NoteSyncController.class, NoteSyncController.PATH);
        servletContextHandler.addServlet(SignInController.class, SignInController.PATH);
        servletContextHandler.addServlet(SignOutController.class, SignOutController.PATH);
        servletContextHandler.addServlet(SignUpController.class, SignUpController.PATH);
        return servletContextHandler;
    }

    private static void startJobs() {
        final var noteGraveCleaner = new NoteGraveCleaner(
            NoteGraveCleanerConfig.ENABLED,
            NoteGraveCleanerConfig.RATE,
            NoteGraveCleanerConfig.THRESHOLD
        );
        new Thread(noteGraveCleaner).start();
    }
}
