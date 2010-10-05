package no.steria.kata.javaee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

public class CreatePersonForm {

    private String lastName = "";
    private String firstName = "";
    private String lastNameValidationError;
    private String firstNameValidationError;

    public CreatePersonForm(HttpServletRequest req) {
        setFirstName(req.getParameter("first_name"));
        setLastName(req.getParameter("last_name"));
    }

    public CreatePersonForm() {
        // TODO Auto-generated constructor stub
    }

    static String htmlEscape(String fullName) {
        if (fullName == null) return "";
        return fullName.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    void show(PrintWriter writer) throws IOException {
        VelocityEngine engine = new VelocityEngine();
        Context context = new VelocityContext();
        context.put("first_name", htmlEscape(firstName));
        context.put("first_name_error", firstNameValidationError);
        context.put("last_name", htmlEscape(lastName));
        context.put("last_name_error", lastNameValidationError);
        engine.evaluate(context, writer, "/person/create.html.vm", new InputStreamReader(getClass().getResourceAsStream("/person/create.html.vm")));
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        setLastNameErrorMessage(validateName(lastName));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        setFirstNameErrorMessage(validateName(firstName));
    }

    public void setLastNameErrorMessage(String lastNameValidationError) {
        this.lastNameValidationError = lastNameValidationError;
    }

    public void setFirstNameErrorMessage(String firstNameValidationError) {
        this.firstNameValidationError = firstNameValidationError;
    }

    static String validateName(String fullName) {
        String errorMessage = null;
        if (fullName == null || fullName.equals("")) {
            errorMessage = "must be given";
        } else if (containsIllegalCharacters(fullName)) {
            errorMessage = "contains illegal characters";
        }
        return errorMessage;
    }

    static boolean containsIllegalCharacters(String fullName) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (fullName.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    public boolean hasErrors() {
        return lastNameValidationError != null || firstNameValidationError != null;
    }

}
