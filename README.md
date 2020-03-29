# quarkus.graphql

This project should demonstrate how to build a GraphQL server with Quarkus native build and gradle.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./gradlew quarkusDev
```

## Packaging and running the application

The application can be packaged using `./gradlew quarkusBuild`.
It produces the `quarkus.graphql-1.0.0-SNAPSHOT-runner.jar` file in the `build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/quarkus.graphql-1.0.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:
```
./gradlew quarkusBuild --uber-jar
```

## Creating a native executable

This depends on if you installed GraalVM on your machine or not. If you don't you can still use it. Docker is your friend.

### No GraalVM
Run the native executable build in a container using: `./gradlew buildNative --docker-build=true`.
You can then build a docker image and execute a container using `./buildImageAndRunContainer.sh`.

### Local GaalVM installation
You can create a native executable using: `./gradlew buildNative`.
You can then execute your native executable with: `./build/quarkus.graphql-1.0.0-SNAPSHOT-runner`

## Quarkus Native Image Specifics

I had to do some extra work to make it run as a native image. Although it is very little. Both changes are described
in greater detail at quarkus documentation: [Quarkus - Tips for writing native applications](https://quarkus.io/guides/writing-native-applications-tips#registering-for-reflection)

### Annotate classes that need runtime reflection

The classes `Author` and `Book` are targest of reflection at runtime. We can just add the annotation `@io.quarkus.runtime.annotations.RegisterForReflection`
at the top of the class. thats it.

### Make schema.graphqls available at runtime

Resources are not all available at runtime. You need to register them. But that is very easy to do:

Add this lines to your build.gradle:

```
buildNative {
    additionalBuildArgs = [
            '-H:ResourceConfigurationFiles=./resources-config.json'
    ]
}
``` 

And place the file resources-config.json in `src/main/resources`:

```json
{
  "resources": [
    {
      "pattern": ".*\\.graphqls$"
    }
  ]
}
```