# 01-07-2026

## 2.0.1

- MOEN-44438: Gradle wrapper updated from `8.6` to `9.3.1`
- MOEN-44438: `gradle/libs.versions.toml` updates
  |                       Library / Plugin                              | Current Version     | Next Version       |
  |:-------------------------------------------------------------------:|:-------------------:|:------------------:|
  | org.jetbrains.kotlin:kotlin-stdlib                                  | 2.0.0               | 2.3.20             |
  | org.jetbrains.kotlin.jvm (plugin)                                   | 2.0.0               | 2.3.20             |
  | org.jetbrains.kotlin.plugin.serialization (plugin)                  | 2.0.0               | 2.3.20             |
  | org.jetbrains.kotlinx:kotlinx-serialization-json                    | 1.7.1               | 1.11.0             |
  | com.squareup.retrofit2:retrofit (+ converters)                      | 2.11.0              | 3.0.0              |
  | com.gradle.plugin-publish (plugin)                                  | 1.2.1               | 2.1.1              |

# 4-3-2025

## 1.1.0
- Improve logging message for missing configuration.
- Support for snapshot builds in Central Portal.

# 18-10-2024

## 1.0.0
- Merging the credentials config flag for OSS Portal
- Adding option to configure retry count for sonatype and timeout duration for all network call
- Logging improvements
- Bugfix
  - Incorrect convertor added for central portal publishing
  - No error shown while the checks are failed in OSS portal during transitioning
- Breaking Changes
  |                       Then                 |                       Now                                      |
  |:------------------------------------------:|:--------------------------------------------------------------:|
  |  s01_oss_mavenCentralUsername              |            oss_mavenCentralUsername                            |
  |  s01_oss_mavenCentralPassword              |            oss_mavenCentralPassword                            |