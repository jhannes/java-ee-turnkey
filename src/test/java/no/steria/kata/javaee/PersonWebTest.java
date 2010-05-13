package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

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

	@Test
	public void shouldFindCreatedPeople() throws Exception {
		jdbcDataSource dataSource = new jdbcDataSource();
		dataSource.setDatabase("jdbc:hsqldb:mem:personWebTest");
		dataSource.setUser("sa");
		new EnvEntry("jdbc/personDs", dataSource);

		Server server = new Server(0);
		server.addHandler(new WebAppContext("src/main/webapp", "/"));
		server.start();
		int serverPort = server.getConnectors()[0].getLocalPort();

		String baseUrl = "http://localhost:" + serverPort + "/";

		WebDriver browser = createWebDriver();

		browser.get(baseUrl);
		browser.findElement(By.linkText("Create new person")).click();
		browser.findElement(By.name("full_name")).sendKeys("Johannes Brodwall");
		browser.findElement(By.name("create")).click();

		browser.get(baseUrl);
		browser.findElement(By.linkText("Search for people")).click();
		browser.findElement(By.name("name_query")).sendKeys("brodw");
		browser.findElement(By.name("find")).click();

		assertThat(browser.getPageSource()).contains("Johannes Brodwall");
	}

	private WebDriver createWebDriver() {
		return new HtmlUnitDriver() {
			@Override
			public WebElement findElement(By by) {
				try {
					return super.findElement(by);
				} catch (NoSuchElementException e) {
					throw new NoSuchElementException("Could not find " + by + " in " + getPageSource());
				}
			}
		};
	}

}
