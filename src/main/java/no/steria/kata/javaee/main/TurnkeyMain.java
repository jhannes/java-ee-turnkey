package no.steria.kata.javaee.main;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.http.security.Password;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.hibernate.cfg.Environment;

public class TurnkeyMain extends AbstractServerMain {

    public static void main(String[] args) throws Exception {
        new TurnkeyMain().start();
    }

    protected void start() throws NamingException, SQLException, Exception {
        extractConfiguration("personweb.properties");
        extractConfiguration("log4j-personweb.properties");
        mergeIntoSystemProperties("personweb.properties");
        PropertyConfigurator.configureAndWatch("log4j-personweb.properties", 10000);
        System.setProperty(Environment.HBM2DDL_AUTO, "update");

        createAndRegisterDatasource("personweb", "jdbc/personDs");

        int port = Integer.parseInt(System.getProperty("personweb.http.port", "7777"));
        HandlerCollection webContexts = createWebContexts();

        startJetty(port, webContexts);
    }

    @Override
    protected HandlerCollection createWebContexts() throws Exception {
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[] {
                new StatusHandler("/personweb-status", "PersonWeb", getMonitoredDataSources(), getLatestVisitorLog()),
                createCurrentWebApp("/person", securityHandler()),
                //new MovedContextHandler(handlerCollection, "/", "/person")
        });
        return handlerCollection;
    }

    protected ConstraintSecurityHandler securityHandler() throws Exception {
        Constraint constraint = new Constraint(Constraint.__BASIC_AUTH, "PERSONWEB");
        constraint.setAuthenticate(true);
        
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/secure/*");
        mapping.setConstraint(constraint);
        
        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
//        securityHandler.addConstraintMapping(mapping);
        
        HashLoginService service = new HashLoginService();
        service.putUser("user1", new Password("password"), new String[] { "PERSONWEB" });
        service.putUser("user2", new Password("password"), new String[] { "PERSONWEB" });
        securityHandler.setLoginService(service);
        securityHandler.setAuthenticator(new BasicAuthenticator());
        return securityHandler;
    }
}
