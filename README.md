![Logo](/.github/logo.png)

# MoEngage Maven Publish Plugin

Gradle plugin to manage the publishing artifacts/libraries to [MavenCentral](https://mvnrepository.com/). The plugin
supports publishing

- Android Library
- Kotlin Library
- Java Library
- Version Catalog.

The plugin automatically closes the uploaded repositories when the publish command is executed, you need not login the
to the web portal to publish the repository.

## Setup

The following steps are required to publish an artifact to Maven Central.

### Configure Library Meta data

Add the below required details in `gradle.properties` of the project or module(in case of a multi-module project).

```properties
# version of your library, example 1.0, 1.1, 1.0.0, etc.
VERSION_NAME=<Version of the library>
# name space of the library, generally reverse of domain name, example com.moengage
GROUP=<Library Group Id>
# name of module or library
ARTIFACT_NAME=<Library Artifact Id>
# portal where you want to publish the library
HOST=<OSS_PORTAL / S01_OSS_PORTAL / CENTRAL_PORTAL>
# for a multi-variant library configure the variant to be released. Optional value, defaults to "release" if not configured.
RELEASE_VARIANT=<Variant to be release>
```

### POM file configuration

Add the pom file configuration in `gradle.properties` of the project or module(in case of a multi-module project).

```properties
NAME=<name>
POM_DESCRIPTION=<description for the library>
POM_URL=<pom url>
POM_LICENCE_NAME=<license name>
POM_LICENCE_URL=<license url>
POM_DEVELOPER_ID=<developer id>
POM_DEVELOPER_NAME=<developer name>
# optionally set the email-id to contact the developer/author of the library.
POM_DEVELOPER_EMAIL=<developer email>
POM_SCM_URL=<scm url>
POM_SCM_CONNECTION=<scm connection>
POM_SCM_DEV_CONNECTION=<scm dev connection>
```

### Publishing Credential Setup

Based on the `HOST` used in the configuration of your Library meta-data add the publishing credentials to the
environment or `gradle.properties` file. Here username password isn't the credentials used for logging into your
account. The username password here would be the generated access token.

```properties
# For OSS_PORTAL & S01_OSS_PORTAL
oss_mavenCentralUsername=<username>
oss_mavenCentralPassword=<password>
# For CENTRAL_PORTAL
mavenCentralUsername=<username>
mavenCentralPassword=<password>
```

When publishing to OSSRH be it S01_OSS_PORTAL or OSS_PORTAL additionally configure the profile id in the environment
variable or in the `gradle.properties` file. Refer to the OSSRH documentation to know more about profile id.

```properties
profileId=<profileId>
```

### Singing Configuration

The artifacts to be uploaded can be signed with a signing key file or using a in-memory singing key file. The in-memory
signing key is the preferred choice for Deployment pipelines like Github Actions, etc.
The signing configuration can be added to the `gradle.properties` file of the project or Gradle home of the project or
as an environment variable.
By default, signing using a file is enabled.

#### Using file as signing configuration

Add the below keys with corresponding values to the `gradle.properties` file or add them as the environment variable.

```properties
signing.keyId=<key id>
signing.password=<password>
signing.secretKeyRingFile=<signing key file path>
```

#### In-Memory signing configuration

Enable in-memory signing for the project or module by adding the below property in the `gradle.properties` of your
project/module.

```properties
IN_MEMORY_SIGNING=true
```

Add the below keys with corresponding values to the `gradle.properties` file or add them as the environment variable.

```properties
signingInMemoryKeyId=<key id>
signingInMemoryKey=<key>
signingInMemoryKeyPassword=<password>
```

*Note: Do not push the credentials or singing key, password, ring file to version control.*

## Plugin Integration

- Add the plugin in your project level `build.gradle(.kts)`

```kotlin
plugins {
    id("com.moengage.plugin.maven.publish").version("0.0.2").apply(false)
}
```

- Add the plugin in your module level `build.gradle(.kts)`

```kotlin
plugins {
    id("com.moengage.plugin.maven.publish").version("0.0.2")
}
```

## Publishing Library

To publish/upload a library to Maven Central use the below command.

```shell
./gradlew publishToMavenRepository
```

### Snapshot builds

To publish snapshot builds append `-SNAPSHOT` to the version name and run the publishing command. The plugin will
automatically publish a snapshot build.

## Plugin Configuration

### Logging

By default, plugin print only the required information in the console which is `Level.NOTICE`. To configure the log
level,
add the below properties in your `gradle.properties` file or pass the property.

```properties

# NO_LOG(0)
# ERROR(1)
# WARNING(2)
# NOTICE(3)
# VERBOSE(4)
LOG_LEVEL=<0/1/2/3/4>
```

To pass the property through command line instead of adding in `gradle.properties` file use the below command while
executing publish command.

```shell
./gradlew publishToMavenRepository -PLOG_LEVEL=<0/1/2/3/4>
```

### Network

To configure the timeout for the network call add the following property in `gradle.properties` file

```properties
NETWORK_TIMEOUT=<INT_VALUE (default 60)>
```

Sometime running the sonatype APIs can take time greater than the configured or default timeout duration which can
terminate the network with an exception. You can configured the retries count for those timeout failure which will ensure
publishing the repository is not often failing in peek hour for `OSS_PORTAL` and `S01_OSS_PORTAL`.

```properties
SONATYPE_MAX_RETRY=<INT_VALUE (default 0)>
```