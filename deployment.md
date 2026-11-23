Deployment Guide — Salesforce CRUD Manager

1. Prerequisites
- Java 21 or compatible configured JDK
- Git
- Salesforce Developer Edition account
- Salesforce Connected App OAuth credentials

2. Run Locally
1. Clone or download the project.
2. Configure Salesforce credentials as environment variables:
   - `SALESFORCE_CLIENT_ID`
   - `SALESFORCE_CLIENT_SECRET`
3. Ensure the OAuth redirect URI matches the application's configured redirect URI.
4. Open a terminal in the project directory.
5. Run:

```bash
./mvnw spring-boot:run
```

6. Open `http://localhost:8080/index.html`.

3. Deploy to a Hosting Platform
1. Push the project to a Git repository.
2. Create a Java web service on a hosting platform that supports Spring Boot.
3. Set the build command to `./mvnw clean package`.
4. Set the start command to `java -jar target/*.jar`.
5. Configure `SALESFORCE_CLIENT_ID` and `SALESFORCE_CLIENT_SECRET` in the hosting platform's environment settings.
6. Configure the application's server port to use the platform-provided `PORT` environment variable.
7. Update the OAuth redirect URI to the deployed application's URL plus `/login/oauth2/code/salesforce`.
8. Add the same redirect URI to the Salesforce Connected App settings.
9. Redeploy and test login and CRUD operations.

4. Post-Deployment Testing
- Verify Salesforce OAuth login.
- Test Create, Read, Update, and Delete for all five objects.
- Test search and pagination.
- Verify error handling.

5. Security Notes
- Never commit client secrets or access tokens to Git.
- Store credentials as hosting-platform environment variables.
- Use HTTPS for production deployment.
- Confirm the production OAuth redirect URI matches exactly.