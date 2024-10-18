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