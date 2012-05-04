A demonstration of a Java application deployed as an executable war
with all dependencies included.

Features:
=========
* Startup/shutdown
* Status page
* Database migrations (TODO)
* Heroku ready (TODO)
* Logging with logback
* Building with gradle
* LDAP authentication/authorization (TODO)
* Oracle SSO authentication/authorization (TODO)

From git to Eclipse/IDEA:
-------------------------
Prerequisites: Java 7, Eclipse/IDEA

1. Download the project: <code>git clone git@github.com:steria/java-ee-turnkey</code>
2. Build the Eclipse project definition: <code>gradlew eclipse</code> (alternatively: <code>gradlew idea</code>)
3. Import project into Eclipse/IDEA
4. Run all tests
5. Start <code>no.steria.turnkey.main.TurnkeyMain</code> as a main class
6. Verify by going to http://localhost:5001

From git to local running application:
--------------------------------------
Prerequisites: Java 7

1. Download the project: <code>git clone git@github.com:steria/java-ee-turnkey</code>
2. Build the project: <code>gradlew jetty</code> (alternatively: <code>gradlew idea</code>)
3. Set up local environment
  * Configure web server port: <code>set PORT=5001</code> (UNIX: <code>export PORT=5001</code>)
4. Start server: <code>java -jar target/*.war</code>
5. Verify by going to http://localhost:5001

From git to heroku:
-------------------
Prerequisites: Java 7, "Heroku Toolbelt":https://toolbelt.herokuapp.com/

1. Download the project: <code>git clone git@github.com:steria/java-ee-turnkey</code>
2. Sign up for Heroku at https://api.heroku.com/signup
3. Verify signup and installation of toolbelt: <code>heroku login</code>
4. Create a heroku project: <code>heroku create --stack cedar _instance_name_</code>
5. Push the code: <code>git push heroku master</code>
6. Open the web browser on your project: <code>heroku open</code>

For more info, see https://devcenter.heroku.com/
