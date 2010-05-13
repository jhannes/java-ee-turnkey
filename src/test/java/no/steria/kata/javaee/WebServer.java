package no.steria.kata.javaee;

import org.hsqldb.jdbc.jdbcDataSource;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebServer {

	public static void main(String[] args) throws Exception {
		jdbcDataSource dataSource = new jdbcDataSource();
		dataSource.setDatabase("jdbc:hsqldb:file:src/test/db/personDatabase");
		dataSource.setUser("sa");
		new EnvEntry("jdbc/personDs", dataSource);

		Server server = new Server(8088);
		server.addHandler(new WebAppContext("src/main/webapp", "/"));
		server.start();

		System.out.println("http://localhost:8088/");
	}
}
