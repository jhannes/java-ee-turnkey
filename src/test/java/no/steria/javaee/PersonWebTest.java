package no.steria.javaee;


import static org.fest.assertions.Assertions.assertThat;

import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.webapp.WebAppContext;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class PersonWebTest {

	private static final int SERVER_PICKS_PORT = 0;
	private String baseUrl;

	@Test
	public void shouldCreateAPersonAndListIt() throws Exception {
		System.setProperty(Environment.HBM2DDL_AUTO, "create");
		jdbcDataSource dataSource = new jdbcDataSource();
		dataSource.setDatabase("jdbc:hsqldb:mem:webtesting");
		dataSource.setUser("sa");
		new EnvEntry("jdbc/personDs", dataSource);
		
		Server server = new Server(SERVER_PICKS_PORT);
		server.addHandler(new WebAppContext("src/main/webapp", "/"));
		server.start();
		
		int port = server.getConnectors()[0].getLocalPort();
		baseUrl = "http://localhost:" + port + "/";
		
		WebDriver browser = createWebDriver();
		browser.get(baseUrl);
		browser.findElement(By.linkText("Create person")).click();
		browser.findElement(By.name("full_name")).sendKeys("Darth Vader");
		browser.findElement(By.name("createPerson")).click();
		
		browser.get(baseUrl);
		browser.findElement(By.linkText("Find people")).click();
		browser.findElement(By.name("name_query")).sendKeys("Vader");
		browser.findElement(By.name("findPeople")).click();
		
		assertThat(browser.getPageSource()).contains("Darth Vader");
	}

	private HtmlUnitDriver createWebDriver() {
		return new HtmlUnitDriver() {
			@Override
			public WebElement findElement(By by) {
				try {
					return super.findElement(by);
				} catch (NoSuchElementException e) {
					throw new NoSuchElementException("Can't find " + by + " in " + getPageSource());
				}
			}
		};
	}
}
