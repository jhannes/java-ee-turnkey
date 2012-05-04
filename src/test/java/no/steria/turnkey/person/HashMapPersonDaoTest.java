package no.steria.turnkey.person;

import no.steria.turnkey.common.ConnectionPool;

import org.eclipse.jetty.plus.jndi.EnvEntry;

public class HashMapPersonDaoTest extends AbstractPersonDaoTest {

    @Override
    protected PersonDao createPersonDao() throws Exception {
        new EnvEntry("jdbc/testDs", ConnectionPool.fromSystemProperties("personDs"));
        return new HashMapPersonDao("jdbc/testDs");
    }

}
