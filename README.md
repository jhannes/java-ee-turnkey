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

Either: <code>gradlew build && java -jar build/libs/*.war</code><br>
Verify by going to http://localhost:10080/person

or

<code>gradlew run</code><br>
Verify by going to http://localhost:8888/person

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
