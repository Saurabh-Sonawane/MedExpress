**Notes / Trade-offs**

1. Consultations and questions are stored in memory for simplicity, so nothing is persistent.
2. All answer values are stored as Json type. This makes handling JSON easier, and the evaluator interprets the actual type.
3. We use a single submit-answers endpoint. This keeps frontend integration simple, but it means partial consultations cannot be saved.
4. The questions are currently hardcoded. It’s easy to change them.
5. The evaluation logic is simplified to a true/false decision. Real doctor rules or more complex checks are not implemented yet.
6. Basic validation is in place: if userId is missing or answers are empty, the API returns a 400 Bad Request. It needs more validation to verify userId with persistence layer.

**Running the Application**

```./gradlew bootRun```

**Testing the Application**

Postman collection is added in the repo at `/postman` folder.