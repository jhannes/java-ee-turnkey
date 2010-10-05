package no.steria.kata.javaee;

import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebServer {

    public static void main(String[] args) throws Exception {
        String jndiDataSource = "jdbc/personDs";

        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:file:target/test-db");
        dataSource.setUser("sa");
        new EnvEntry(jndiDataSource, dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "update");

        Server server = new Server(8088);
        server.addHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();

        int localPort = server.getConnectors()[0].getLocalPort();
        String baseUrl = "http://localhost:" + localPort + "/";
        System.out.println(baseUrl);
    }
}
