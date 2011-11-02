package no.steria.kata.javaee.main;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import no.steria.kata.javaee.main.AbstractServerMain.LatestVisitorLog;

import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class StatusHandler extends AbstractHandler {

    private final List<String> monitoredDataSources;
    private String applicationName;
    private final LatestVisitorLog latestVisitorLog;
    private String contextPath;

    enum Severity {
        FATAL("red"), 
        WARNING("orange"),
        SUSPECT("yellow"),
        OK("green");
        public final String color;
        private Severity(String color) {
            this.color = color;
        }
    }
    
    private static class StatusInfo {
        Severity severity;
        String category;
        String message;
        public StatusInfo(String category, Object message, Severity severity) {
            this.category = category;
            this.message = String.valueOf(message);
            this.severity = severity;
        }
        @Override
        public String toString() {
            return category + "=" + severity.name() + " (" + message + ")";
        }
    }
    
    
    public StatusHandler(String contextPath, String applicationName, List<String> monitoredDataSources, LatestVisitorLog latestVisitorLog) {
        this.applicationName = applicationName;
        this.monitoredDataSources = monitoredDataSources;
        this.latestVisitorLog = latestVisitorLog;
        this.contextPath = contextPath;
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!target.equals(contextPath)) {
            return;
        }
        
        baseRequest.setHandled(true);
        
        response.setContentType("text/html");
        
        List<StatusInfo> statusInfoList = aggregateStatusInfo();
        Severity severity = getHighestSeverity(statusInfoList);
        response.getWriter().println("<html>");
        response.getWriter().println("<head>");
        response.getWriter().println("<title>" + applicationName + ": " + severity + "</title>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body style='background-color: " + severity.color + "'>");
        response.getWriter().println("<h2>" + applicationName + ": " + severity + "</h2>");
        response.getWriter().println("<ul>");
        for (StatusInfo statusInfo : statusInfoList) {
            response.getWriter().println("<li>" + statusInfo + "</li>");
        }
        response.getWriter().println("<ul>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
        response.getWriter().flush();
        
        response.setStatus(HttpServletResponse.SC_OK);
        HttpConnection.getCurrentConnection().getRequest().setHandled(true);
    }

    private Severity getHighestSeverity(List<StatusInfo> statusInfoList) {
        List<StatusInfo> copy = new ArrayList<StatusHandler.StatusInfo>(statusInfoList);
        Collections.sort(copy, new Comparator<StatusInfo>() {
            public int compare(StatusInfo o1, StatusInfo o2) {
                return o1.severity.compareTo(o2.severity);
            }
        });
        return copy.isEmpty() ? Severity.OK : copy.get(0).severity;
    }

    private List<StatusInfo> aggregateStatusInfo() {
        ArrayList<StatusInfo> result = new ArrayList<StatusHandler.StatusInfo>();
        result.add(statusServerInfo());
        result.add(statusUptime());
        for (String dataSource : monitoredDataSources) {
            result.add(statusDatabase(dataSource));
        }
        result.add(statusLatestUserRequest());
        return result;
    }

    private StatusInfo statusServerInfo() {
        int serverPort = getServer().getConnectors()[0].getLocalPort();
        URL codeLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        return new StatusInfo("server info", "Port: " + serverPort + ", codebase: " + codeLocation, Severity.OK);
    }

    private StatusInfo statusLatestUserRequest() {
        Period warningPeriod = Period.seconds(10);
        String lastVisitMessage = "Last visit " + latestVisitorLog.getLatestRequest();
        if (latestVisitorLog.getLatestRequest().isBefore(new DateTime().minus(warningPeriod))) {
            return new StatusInfo("lastest visitor", "More than " + warningPeriod + " since last visitor. " + lastVisitMessage, Severity.SUSPECT);
        }
        return new StatusInfo("lastest visitor", lastVisitMessage, Severity.OK);
    }

    private StatusInfo statusDatabase(String dsName) {
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup(dsName);
            dataSource.getConnection().close();
            return new StatusInfo("database:" + dsName, "Ok", Severity.OK);
        } catch (SQLException e) {
            return new StatusInfo("database:" + dsName, e.getMessage(), Severity.FATAL);
        } catch (NamingException e) {
            return new StatusInfo("database:" + dsName, e.getMessage(), Severity.FATAL);
        }
    }

    private StatusInfo statusUptime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        DateTime startTime = new DateTime(runtimeMXBean.getStartTime());
        return new StatusInfo("uptime", new Interval(startTime, new DateTime()).toDuration(), Severity.OK);
    }

}