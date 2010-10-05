package no.steria.kata.javaee;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

public class CreatePersonForm {

    private String lastName;
    private String firstName;
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

    void show(PrintWriter writer) {
        writer.append("<html>");
        writer.append("<head><style>.error { color: red; }</style></head>");

        if (firstNameValidationError != null) {
            writer.append("<div class='error'>First name ").append(firstNameValidationError).append("</div>");
        }
        if (lastNameValidationError != null) {
            writer.append("<div class='error'>Last name ").append(lastNameValidationError).append("</div>");
        }
        writer //
            .append("<form method='post' action='createPerson.html'>") //
            .append("<label>First name:</label>")
            .append("<input type='text' name='first_name' value='" + htmlEscape(firstName) + "'/>") //
            .append("<label>Last name:</label>")
            .append("<input type='text' name='last_name' value='" + htmlEscape(lastName) + "'/>") //
            .append("<input type='submit' name='createPerson' value='Create person'/>") //
            .append("</form>");
        writer.append("</html>");
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
