**Notes / Trade-offs**

1. Consultations and questions are stored in memory for simplicity, so nothing is persistent.
2. All answer values are stored as Json type. This makes handling JSON easier, and the evaluator interprets the actual type.
3. The questions are currently hardcoded. It’s easy to change them.
4. The evaluation logic is simplified to a true/false decision. Real doctor rules or more complex checks are not implemented yet.
5. Basic validation is in place: if userId is missing or answers are empty, the API returns a 400 Bad Request. It needs more validation to verify userId with persistence layer.

**System Requirement**

For running this application you need Java 17 and above.

**Running the Application**

```./gradlew bootRun```

**Testing the Application**

Postman collection is added in the repo at `/postman` folder.