package no.steria.turnkey.common.jetty;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class StatusHandler extends AbstractHandler {

    private final List<String> monitoredDataSources;
    private String applicationName;
    private long latestRequestTime = System.currentTimeMillis();
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


    public StatusHandler(String contextPath, String applicationName, List<String> monitoredDataSources) {
        this.applicationName = applicationName;
        this.monitoredDataSources = monitoredDataSources;
        this.contextPath = contextPath;
    }

    @Override
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
            @Override
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
        long warningPeriodInSeconds = 30;
        String lastVisitMessage = "Last visit " + new Date(latestRequestTime);
        if (latestRequestTime + warningPeriodInSeconds*1000 < System.currentTimeMillis()) {
            return new StatusInfo("lastest visitor", "More than " + warningPeriodInSeconds + " seconds since last visitor. " + lastVisitMessage, Severity.SUSPECT);
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
        long uptime = System.currentTimeMillis() - runtimeMXBean.getStartTime();
        return new StatusInfo("uptime (seconds)", uptime/1000.0, Severity.OK);
    }

}