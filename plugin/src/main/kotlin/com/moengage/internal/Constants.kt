package com.moengage.internal

// Log constants
internal const val BASE_TAG = "GradleMavenPublishPlugin"

// Task Name
internal const val AUTO_RELEASE_TASK_NAME = "publishToMavenRepository"
internal const val MAVEN_PUBLISH_TASK_NAME = "publish"

// Identifiers
internal const val SNAPSHOT_IDENTIFIER = "-SNAPSHOT"

// Host Urls
internal const val OSS_PORTAL_HOST = "https://oss.sonatype.org/service/local/"
internal const val OSS_PORTAL_STAGING_HOST = "https://oss.sonatype.org/content/repositories/snapshots/"

internal const val S01_OSS_PORTAL_HOST = "https://s01.oss.sonatype.org/service/local/"
internal const val S01_OSS_PORTAL_STAGING_HOST = "https://s01.oss.sonatype.org/content/repositories/snapshots/"

internal const val CENTRAL_PORTAL_HOST = "https://central.sonatype.com/api/v1/"

// Default values
internal const val DEFAULT_RELEASE_VARIANT = "release"

// Configuration options via gradle properties
internal const val RELEASE_VARIANT = "RELEASE_VARIANT"
internal const val SIGNING_TYPE = "IN_MEMORY_SIGNING"
internal const val RELEASE_HOST = "HOST"

// Credentials
internal const val PROFILE_ID = "profileId"
internal const val SO1_OSS_MAVEN_CENTER_USER_NAME = "s01_oss_mavenCentralUsername"
internal const val SO1_OSS_MAVEN_CENTER_PASSWORD = "s01_oss_mavenCentralPassword"
internal const val OSS_MAVEN_CENTER_USER_NAME = "oss_mavenCentralUsername"
internal const val OSS_MAVEN_CENTER_PASSWORD = "oss_mavenCentralPassword"
internal const val MAVEN_CENTER_USER_NAME = "mavenCentralUsername"
internal const val MAVEN_CENTER_PASSWORD = "mavenCentralPassword"

// Signing details
internal const val SIGNING_IN_MEMORY_KEY = "signingInMemoryKey"
internal const val SIGNING_IN_MEMORY_KEY_ID = "signingInMemoryKeyId"
internal const val SIGNING_IN_MEMORY_KEY_PASSWORD = "signingInMemoryKeyPassword"

// Pom file properties
internal const val GROUP = "GROUP"
internal const val ARTIFACT_NAME = "ARTIFACT_NAME"
internal const val VERSION_NAME = "VERSION_NAME"
internal const val NAME = "NAME"
internal const val POM_DESCRIPTION = "POM_DESCRIPTION"
internal const val POM_URL = "POM_URL"
internal const val POM_LICENCE_NAME = "POM_LICENCE_NAME"
internal const val POM_LICENCE_URL = "POM_LICENCE_URL"
internal const val POM_DEVELOPER_ID = "POM_DEVELOPER_ID"
internal const val POM_DEVELOPER_NAME = "POM_DEVELOPER_NAME"
internal const val POM_DEVELOPER_EMAIL = "POM_DEVELOPER_EMAIL"
internal const val POM_SCM_URL = "POM_SCM_URL"
internal const val POM_SCM_CONNECTION = "POM_SCM_CONNECTION"
internal const val POM_SCM_DEV_CONNECTION = "POM_SCM_DEV_CONNECTION"

// Log Configuration
internal const val LOG_LEVEL = "LOG_LEVEL"

// Network configuration
internal const val NETWORK_CALL_TIMEOUT_DURATION = "NETWORK_TIMEOUT"
internal const val NEXUS_REPOSITORY_MAX_RETRY_ON_TIMEOUT = "SONATYPE_MAX_RETRY"
