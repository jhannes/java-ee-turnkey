package no.steria.turnkey.common;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPool {

    static {
        registerDriver("oracle.jdbc.driver.OracleDriver");
        registerDriver("org.h2.Driver");
    }

    public static DataSource create(String url, String username, String password) {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setAcquireRetryAttempts(1);
        dataSource.setAcquireIncrement(1);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private static void registerDriver(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            // OK, ignored
        }
    }

    public static DataSource create(String url) {
        return create(url, "sa", "");
    }

    public static DataSource fromSystemProperties(String dsName) {
        String serverUrl = System.getProperty("datasource." + dsName + ".url",
            "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=Oracle");
        String username = System.getProperty("datasource." + dsName + ".username", dsName);
        String password = System.getProperty("datasource." + dsName + ".password", username);
        System.out.println("Using data source " + dsName + " (url=" + serverUrl + ",username=" + username + ")");
        return create(serverUrl, username, password);
    }

}

