package no.steria.kata.javaee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public abstract class VelocityForm {

    protected final HttpServletRequest req;
    protected Map<String, String> errors = new HashMap<String, String>();
    private Map<String,Object> modelVariables = new HashMap<String, Object>();

    public VelocityForm(HttpServletRequest req) {
        this.req = req;
    }

    protected String getParameter(String parameterName) {
        if (req == null) return "";
        if (req.getParameter(parameterName) == null) return "";
        return req.getParameter(parameterName);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    protected String getRequiredStringParameter(String parameterName, String description) {
        String value = getParameter(parameterName);
        if (value == null || value.equals("")) {
            errors.put(description, "must be given");
        } else if (containsIllegalCharacters(value)) {
            errors.put(description, "contains illegal characters");
        }
        return value;
    }

    protected DateMidnight getDateParameter(String parameterName, String description) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        String birthDateAsString = getParameter(parameterName);
        if (birthDateAsString.length() > 0) {
            try {
                return dateTimeFormatter.parseDateTime(birthDateAsString).toDateMidnight();
            } catch (IllegalArgumentException e) {
                errors.put(description, "must be on the format 'dd.mm.yyyy'");
            }
        }
        return null;
    }

    private boolean containsIllegalCharacters(String fullName) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (fullName.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    protected void renderVelocityTemplate(PrintWriter writer, String templateName) throws IOException {
        VelocityEngine engine = new VelocityEngine();
        Context context = new VelocityContext(modelVariables);
        List<String> parameterNames = getFieldNames();
        for (String parameterName : parameterNames) {
            context.put(parameterName, htmlEscape(getParameter(parameterName)));
        }
        context.put("errors", errors);

        engine.evaluate(context, writer, templateName, new InputStreamReader(getClass().getResourceAsStream(templateName)));
    }

    static String htmlEscape(String name) {
        if (name == null) return "";
        return name.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    protected abstract List<String> getFieldNames();

    public void addModelVariable(String variableName, Object modelObject) {
        this.modelVariables.put(variableName, modelObject);
    }

}
