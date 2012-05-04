package no.steria.turnkey.person;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import no.steria.turnkey.common.web.VelocityForm;

public class FindPersonForm extends VelocityForm {

    public FindPersonForm(HttpServletRequest req) {
        super(req);
    }

    public void show(PrintWriter writer) throws IOException {
        renderVelocityTemplate(writer, "/person/find.html.vm");
    }

    public void setPeople(List<Person> people) {
        addModelVariable("people", people);
    }

    public String getNameQuery() {
        return getParameter("name_query");
    }

    @Override
    protected List<String> getFieldNames() {
        return Arrays.asList("name_query");
    }

}
