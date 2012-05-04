package no.steria.turnkey.person;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import no.steria.turnkey.common.web.VelocityForm;


public class CreatePersonForm extends VelocityForm {

    public CreatePersonForm(HttpServletRequest req) {
        super(req);
    }

    void show(PrintWriter writer) throws IOException {
        renderVelocityTemplate(writer, "/person/create.html.vm");
    }

    @Override
    protected List<String> getFieldNames() {
        return Arrays.asList("first_name", "last_name", "birth_date");
    }

    public Person createPerson() {
        Person person = Person.withName(getRequiredStringParameter("first_name", "First name"), getRequiredStringParameter("last_name", "Last name"));
        person.setBirthDate(getDateParameter("birth_date", "Birth date"));
        return person;
    }
}
