package no.steria.kata.javaee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
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

public class CreatePersonForm {

    private HttpServletRequest req;
    private Map<String, String> errors = new HashMap<String, String>();

    public CreatePersonForm(HttpServletRequest req) {
        this.req = req;
    }

    public CreatePersonForm() {
    }

    static String htmlEscape(String name) {
        if (name == null) return "";
        return name.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    void show(PrintWriter writer) throws IOException {
        VelocityEngine engine = new VelocityEngine();
        Context context = new VelocityContext();
        List<String> parameterNames = Arrays.asList("first_name", "last_name", "birth_date");
        for (String parameterName : parameterNames) {
            context.put(parameterName, htmlEscape(getParameter(parameterName)));
        }

        context.put("errors", errors);

        engine.evaluate(context, writer, "/person/create.html.vm", new InputStreamReader(getClass().getResourceAsStream("/person/create.html.vm")));
    }

    private String getParameter(String parameterName) {
        if (req == null) return "";
        if (req.getParameter(parameterName) == null) return "";
        return req.getParameter(parameterName);
    }

    private String getRequiredStringParameter(String parameterName, String description) {
        String value = getParameter(parameterName);
        if (value == null || value.equals("")) {
            errors.put(description, "must be given");
        } else if (containsIllegalCharacters(value)) {
            errors.put(description, "contains illegal characters");
        }
        return value;
    }

    static boolean containsIllegalCharacters(String fullName) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (fullName.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Person createPerson() {
        Person person = Person.withName(getRequiredStringParameter("first_name", "First name"), getRequiredStringParameter("last_name", "Last name"));
        person.setBirthDate(getBirthDate());
        return person;
    }

    private DateMidnight getBirthDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        String birthDateAsString = req.getParameter("birth_date");
        if (birthDateAsString != null && birthDateAsString.length() > 0) {
            try {
                return dateTimeFormatter.parseDateTime(birthDateAsString).toDateMidnight();
            } catch (IllegalArgumentException e) {
                errors.put("Birth date", "must be on the format 'dd.mm.yyyy'");
            }
        }
        return null;
    }

}
