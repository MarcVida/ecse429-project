# ecse429-project

## Part A

### Tool Versions
- **Gradle:** 8.8
- **Groovy:** 3.0.21
- **JVM:** 21 (Oracle Corporation 21+35-LTS-2513)

### Session Notes
Excel session notes are in the subdirectories of ExploratorySessionNotes.

### Running the Tests
To run all the tests, execute the following commands sequentially inside the `UnitTestSuite` project folder:

```bash
gradlew test --tests 'TodoTest' --tests 'ProjectTest' --tests 'CategoryTest'
gradlew test --tests 'ShutdownTest'
```

## Part B

### Tool Versions
- **Gradle:** 8.8
- **Groovy:** 3.0.21
- **JVM:** 21 (Oracle Corporation 21+35-LTS-2513)

### User stories
The user stories are in the `UserStories/UserStories.xlsx` file.

### Running the Tests
To run all the tests, execute the following command inside the `StoryTestSuite` project folder:

```bash
gradlew cucumber
```
