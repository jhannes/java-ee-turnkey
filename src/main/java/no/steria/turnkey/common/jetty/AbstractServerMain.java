package no.steria.turnkey.common.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import no.steria.turnkey.common.ConnectionPool;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public abstract class AbstractServerMain {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<String> monitoredDataSources = new ArrayList<String>();

    protected EnvEntry createAndRegisterDatasource(String dsName, String jndiName) throws NamingException, SQLException {
        this.monitoredDataSources.add(jndiName);
        return new EnvEntry(jndiName, createDataSource(dsName));
    }

    public List<String> getMonitoredDataSources() {
        return monitoredDataSources;
    }

    protected void configureLogging(String logConfig) throws IOException, JoranException {
        extractConfiguration(logConfig);
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        configurator.doConfigure(logConfig);
    }

    protected void startJetty(int port, HandlerCollection webContexts) throws Exception {
        attemptShutdown(port);

        Server server = new Server(port);
        webContexts.addHandler(createShutdownHandler(server));
        server.setHandler(webContexts);

        server.start();

        for (Connector connector : server.getConnectors()) {
            if (connector.isFailed()) {
                System.out.println(connector + " failed");
                logger.error(connector + " failed");
                server.stop();
            }
        }
        for (Handler handler : server.getHandlers()) {
            if (handler.isFailed()) {
                logger.error(handler + " failed");
                server.stop();
            }
        }

        logger.info("Started: http://" + Inet4Address.getLocalHost().getHostName() + ":" + server.getConnectors()[0].getPort());
    }

    private void attemptShutdown(int port) {
        try {
            URL url = new URL("http://localhost:" + port + "/shutdown?token=" + getShutdownToken());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.getResponseCode();
            logger.info("Shutting down " + url + ": " + connection.getResponseMessage());
        } catch (SocketException e) {
            logger.debug("Not running");
            // Okay - the server is not running
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected ShutdownHandler createShutdownHandler(Server server) {
        return new ShutdownHandler(server, getShutdownToken());
    }

    private String getShutdownToken() {
        return System.getProperty("server.shutdownToken", "sdgklnslhawoirnasiln");
    }

    protected void mergeIntoSystemProperties(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            logger.warn("Could not load properties from " + file + ": Resource not found");
            return;
        }
        Properties fileProperties = new Properties();
        fileProperties.load(new FileInputStream(file));
        fileProperties.putAll(System.getProperties());
        System.setProperties(fileProperties);
    }

    protected void extractConfiguration(String configurationFile) throws IOException {
        File file = new File(configurationFile);
        if (file.exists())
            return;

        InputStream resourceAsStream = getClass().getResourceAsStream("/" + configurationFile);
        if (resourceAsStream == null) {
            logBeforeLoggingIsConfigured("Missing configuration file " + configurationFile);
            System.exit(1);
            return;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            int c;
            while ((c = resourceAsStream.read()) != -1)
                fileOutputStream.write((byte) c);
        } finally {
            fileOutputStream.close();
            resourceAsStream.close();
        }
    }

    private void logBeforeLoggingIsConfigured(String message) {
        System.err.println(message);
    }

    protected DataSource createDataSource(String dsName) throws SQLException {
        return ConnectionPool.fromSystemProperties(dsName);
    }

    public AbstractServerMain() {
        super();
    }

    protected abstract HandlerCollection createWebContexts() throws Exception;

    protected Handler createCurrentWebApp(String contextRoot, Handler securityHandler) throws Exception {
        URL[] urls = ((URLClassLoader) AbstractServerMain.class.getClassLoader()).getURLs();
        WebAppContext context;
        if (urls.length == 1 && urls[0].getFile().endsWith(".war")) {
            context = new WebAppContext(urls[0].getFile(), contextRoot);
        } else {
            context = new WebAppContext("src/main/webapp", contextRoot);
        }
        context.setHandler(securityHandler);
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setHandler(context);
        return requestLogHandler;
    }

}