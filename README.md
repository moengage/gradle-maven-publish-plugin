![Logo](/.github/logo.png)

# MoEngage Maven Publish Plugin

Gradle plugin to manage the maven central repository upload and release automatically. Plugin can be used for publishing the Android, Kotlin or Java libraries.

## Setup

### Repository configuration
Add the below required details in `gradle.properties`
```text
VERSION_NAME=<Version of the library>
ARTIFACT_NAME=<Library Artifact Id>
GROUP=<Library Group Id>
HOST=<OSS_PORTAL / S01_OSS_PORTAL / CENTRAL_PORTAL>
```

Add configuration details in `gradle.properties` (optional)
```text
RELEASE_VARIANT=<Variant to be release> (default is "release")
```

### Credentials Setup
- Add the username and password based on the host
```text
# For S01_OSS_PORTAL
s01_oss_mavenCentralUsername
s01_oss_mavenCentralPassword

# For OSS_PORTAL
oss_mavenCentralUsername
oss_mavenCentralPassword

# For CENTRAL_PORTAL
mavenCentralUsername
mavenCentralPassword
```

### Pom file configuration
Add the pom file configuration in `gradle.properties`
```text
NAME=<name>
POM_DESCRIPTION=<description for the library>
POM_URL=<pom url>

POM_LICENCE_NAME=<license name>
POM_LICENCE_URL=<license url>

POM_DEVELOPER_ID=<developer id>
POM_DEVELOPER_NAME=<developer name>
POM_DEVELOPER_EMAIL=<developer email>

POM_SCM_URL=<scm url>
POM_SCM_CONNECTION=<scm connection>
POM_SCM_DEV_CONNECTION=<scm dev connection>
```

### Singing Configuration

#### In-Memory signing configuration (can be used in the workflow pipelines)
- Enable in-memory signing, add the below property in `gradle.properties`
```text
IN_MEMORY_SIGNING=true (default value is false)
```

- Add key details in `gradle.properties`
```text
signingInMemoryKeyId=<key id>
signingInMemoryKey=<key>
signingInMemoryKeyPassword=<password>
```

#### Using files as signing configuration (by default enabled)
- Add key details in `gradle.properties`
```text
signing.keyId=<key id>
signing.password=<password>
signing.secretKeyRingFile=<signing key file path>
```

## Plugin Integration
- Add the plugin in your project level build.gradle.kts
```kotlin
plugins {
    id("com.moengage.plugin.maven.publish").version("<VERSION>").apply(false)
}
```
- Add the plugin in your module level build.gradle.kts
```kotlin
plugins {
   id("com.moengage.plugin.maven.publish").version("<VERSION>")
}
```

## Publishing Library

```shell
./gradlew publishToMavenRepository
```