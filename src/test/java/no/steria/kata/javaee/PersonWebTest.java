package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class PersonWebTest {
	
	private String baseUrl;

	@Test
	public void shouldDisplaySavedPeople() throws Exception {
		Server server = new Server(0);
		server.addHandler(new WebAppContext("src/main/webapp","/"));
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
		browser.findElement(By.name("name_query")).sendKeys("vader");
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
