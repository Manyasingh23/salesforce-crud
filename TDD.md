 Test Design Document (TDD)
 Salesforce CRUD Manager

1. Objective
To test a web application that performs CRUD operations on Salesforce standard objects.

2. Scope
- Account
- Contact
- Lead
- Opportunity
- Case

3. Test Environment
- Frontend: HTML, CSS, JavaScript
- Backend: Java, Spring Boot
- API: Salesforce REST API
- Authentication: Salesforce OAuth 2.0
- Browser: Google Chrome

4. Test Cases

| ID | Test Scenario | Expected Result |
|---|---|---|
| TC01 | Open application | Dashboard loads |
| TC02 | Salesforce login | User authenticates successfully |
| TC03 | Select an object | Selected object's records appear |
| TC04 | View records | Records display in a table |
| TC05 | Create a record | Record is saved |
| TC06 | Edit a record | Changes are saved |
| TC07 | Delete a record | Record is deleted |
| TC08 | Switch between objects | Correct object records appear |
| TC09 | Search records | Matching records appear |
| TC10 | Load more records | Additional records load when available |
| TC11 | Submit invalid data | Validation/error message appears |
| TC12 | API authentication failure | Access is denied or an error is shown |

5. Test Results
CRUD operations for all five objects were manually verified.

Other test cases must be marked Pass or Fail after execution.

6. Conclusion
The application supports Salesforce CRUD operations for Account, Contact, Lead, Opportunity, and Case. Further testing covers search, pagination, validation, and authentication errors.