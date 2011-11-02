package no.steria.kata.javaee.main;

import java.beans.PropertyVetoException;
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

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public abstract class AbstractServerMain {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<String> monitoredDataSources = new ArrayList<String>();
    
    public static class LatestVisitorLog extends AbstractLifeCycle implements RequestLog {

        private DateTime latestRequest = new DateTime();

        public void log(Request request, Response response) {
            if (request.getPathInfo().startsWith("/favicon.ico")) return;
            if (request.getPathInfo().matches("^/[^/]*status")) return;
            this.latestRequest = new DateTime();
        }
        
        public DateTime getLatestRequest() {
            return latestRequest;
        }
    };
    private LatestVisitorLog latestVisitorLog = new LatestVisitorLog();
    
    public LatestVisitorLog getLatestVisitorLog() {
        return latestVisitorLog;
    }

    protected EnvEntry createAndRegisterDatasource(String dsName, String jndiName) throws NamingException, SQLException {
        this.monitoredDataSources.add(jndiName);
        return new EnvEntry(jndiName, createDataSource(dsName));
    }
    
    public List<String> getMonitoredDataSources() {
        return monitoredDataSources;
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
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        String url = System.getProperty(dsName + ".datasource.url", "jdbc:hsqldb:file:target/test-db");
        String username = System.getProperty(dsName + ".datasource.username", "sa");
        try {
			dataSource.setDriverClass(System.getProperty(dsName + ".datasource.driverClassName", "org.hsqldb.jdbcDriver"));
		} catch (PropertyVetoException e) {
			throw new RuntimeException("C3p0IsStupidException", e);
		}
		dataSource.setJdbcUrl(url);
		dataSource.setUser(username);
        dataSource.setPassword(System.getProperty(dsName + ".datasource.password", ""));
        dataSource.setAcquireRetryAttempts(0);


        try {
        	dataSource.getConnection().close();
            logger.info("Connection to " + dsName + " (url=" + url + ", username=" + username + ") is successfull");
        } catch (SQLException e) {
            logger.error("Could not connect to database " + dsName + ": url=" + url + ", username=" + username, e);
            System.exit(1); // <--- Remove this line if we want the server to
                            // start while the database is down. This eliminates
                            // having to restart the server once the database is
                            // up again
        }

        return dataSource;
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
        requestLogHandler.setRequestLog(latestVisitorLog);
        return requestLogHandler;
    }

}