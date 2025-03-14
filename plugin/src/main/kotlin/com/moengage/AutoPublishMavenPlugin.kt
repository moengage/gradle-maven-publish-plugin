/*
 * Copyright (c) 2014-2024 MoEngage Inc.
 *
 * All rights reserved.
 *
 *  Use of source code or binaries contained within MoEngage SDK is permitted only to enable use of the MoEngage platform by customers of MoEngage.
 *  Modification of source code and inclusion in mobile apps is explicitly allowed provided that all other conditions are met.
 *  Neither the name of MoEngage nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *  Redistribution of source code or binaries is disallowed except with specific prior written permission. Any such redistribution must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.moengage

import com.moengage.internal.ARTIFACT_NAME
import com.moengage.internal.AUTO_RELEASE_TASK_NAME
import com.moengage.internal.GROUP
import com.moengage.internal.LOG_LEVEL
import com.moengage.internal.MAVEN_CENTER_PASSWORD
import com.moengage.internal.MAVEN_CENTER_USER_NAME
import com.moengage.internal.MAVEN_PUBLISH_TASK_NAME
import com.moengage.internal.MavenPublishManager
import com.moengage.internal.NAME
import com.moengage.internal.OSS_MAVEN_CENTER_PASSWORD
import com.moengage.internal.OSS_MAVEN_CENTER_USER_NAME
import com.moengage.internal.POM_DESCRIPTION
import com.moengage.internal.POM_DEVELOPER_ID
import com.moengage.internal.POM_DEVELOPER_NAME
import com.moengage.internal.POM_LICENCE_NAME
import com.moengage.internal.POM_LICENCE_URL
import com.moengage.internal.POM_SCM_CONNECTION
import com.moengage.internal.POM_SCM_DEV_CONNECTION
import com.moengage.internal.POM_SCM_URL
import com.moengage.internal.POM_URL
import com.moengage.internal.PROFILE_ID
import com.moengage.internal.RELEASE_HOST
import com.moengage.internal.SIGNING_IN_MEMORY_KEY
import com.moengage.internal.SIGNING_IN_MEMORY_KEY_ID
import com.moengage.internal.SIGNING_IN_MEMORY_KEY_PASSWORD
import com.moengage.internal.SIGNING_TYPE
import com.moengage.internal.VERSION_NAME
import com.moengage.internal.model.ArtifactReleasePortal
import com.moengage.internal.utils.LogLevel
import com.moengage.internal.utils.LoggerConfiguration.configuredLogLevelValue
import com.moengage.internal.utils.log
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin for publishing the android libraries to the maven central portal with auto drop & release
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
@Suppress("unused")
open class AutoPublishMavenPlugin : Plugin<Project> {

    private lateinit var stagedRepositoryId: String

    override fun apply(project: Project) {
        val logLevelProperties = project.findProperty(LOG_LEVEL) as? String?
        if (logLevelProperties != null) {
            configuredLogLevelValue = logLevelProperties.toInt()
        }
        project.validateReleaseSetup()
        project.applyRequiredPlugins()

        val mavenPublishManager = MavenPublishManager(project)
        val repositoryIdProvider = project.provider {
            if (this::stagedRepositoryId.isInitialized) {
                stagedRepositoryId
            } else {
                mavenPublishManager.getStagedRepositoryId().also {
                    stagedRepositoryId = it
                }
            }
        }

        mavenPublishManager.configurePublish(repositoryIdProvider)
        mavenPublishManager.configureSigning()

        project.tasks.register(AUTO_RELEASE_TASK_NAME) {
            description = "Auto publishes repository to MavenCentral"
            group = "publishing"

            dependsOn(project.tasks.named(MAVEN_PUBLISH_TASK_NAME))
            doLast {
                mavenPublishManager.closeAndReleaseRepository(repositoryIdProvider)
            }
        }
    }

    private fun Project.validateReleaseSetup() {
        val propertiesNotIncluded = mutableListOf<String>()
        if (!hasProperty(VERSION_NAME)) propertiesNotIncluded.add(VERSION_NAME)
        if (!hasProperty(ARTIFACT_NAME)) propertiesNotIncluded.add(ARTIFACT_NAME)
        if (!hasProperty(GROUP)) propertiesNotIncluded.add(GROUP)
        if (!hasProperty(RELEASE_HOST)) propertiesNotIncluded.add(RELEASE_HOST)
        if (!hasProperty(NAME)) propertiesNotIncluded.add(NAME)
        if (!hasProperty(POM_DESCRIPTION)) propertiesNotIncluded.add(POM_DESCRIPTION)
        if (!hasProperty(POM_URL)) propertiesNotIncluded.add(POM_URL)
        if (!hasProperty(POM_LICENCE_NAME)) propertiesNotIncluded.add(POM_LICENCE_NAME)
        if (!hasProperty(POM_LICENCE_URL)) propertiesNotIncluded.add(POM_LICENCE_URL)
        if (!hasProperty(POM_DEVELOPER_ID)) propertiesNotIncluded.add(POM_DEVELOPER_ID)
        if (!hasProperty(POM_DEVELOPER_NAME)) propertiesNotIncluded.add(POM_DEVELOPER_NAME)
        if (!hasProperty(POM_SCM_URL)) propertiesNotIncluded.add(POM_SCM_URL)
        if (!hasProperty(POM_SCM_CONNECTION)) propertiesNotIncluded.add(POM_SCM_CONNECTION)
        if (!hasProperty(POM_SCM_DEV_CONNECTION)) propertiesNotIncluded.add(POM_SCM_DEV_CONNECTION)
        val releasePortal = ArtifactReleasePortal.getMavenCentralPortal(project.findProperty(RELEASE_HOST) as? String)
        when (releasePortal) {
            ArtifactReleasePortal.CENTRAL_PORTAL -> {
                if (!hasProperty(MAVEN_CENTER_USER_NAME)) propertiesNotIncluded.add(MAVEN_CENTER_USER_NAME)
                if (!hasProperty(MAVEN_CENTER_PASSWORD)) propertiesNotIncluded.add(MAVEN_CENTER_PASSWORD)
            }

            else -> {
                if (!hasProperty(OSS_MAVEN_CENTER_USER_NAME)) propertiesNotIncluded.add(OSS_MAVEN_CENTER_USER_NAME)
                if (!hasProperty(OSS_MAVEN_CENTER_PASSWORD)) propertiesNotIncluded.add(OSS_MAVEN_CENTER_PASSWORD)
            }
        }
        // profile id is not required for central portal publishing.
        if (releasePortal == ArtifactReleasePortal.OSS_PORTAL || releasePortal == ArtifactReleasePortal.S01_OSS_PORTAL) {
            if (!hasProperty(PROFILE_ID)) propertiesNotIncluded.add(PROFILE_ID)
        }

        val signingType = findProperty(SIGNING_TYPE)
        if (signingType != null && signingType.toString().toBoolean()) {
            if (!hasProperty(SIGNING_IN_MEMORY_KEY_ID)) propertiesNotIncluded.add(SIGNING_IN_MEMORY_KEY_ID)
            if (!hasProperty(SIGNING_IN_MEMORY_KEY)) propertiesNotIncluded.add(SIGNING_IN_MEMORY_KEY)
            if (!hasProperty(SIGNING_IN_MEMORY_KEY_PASSWORD)) propertiesNotIncluded.add(SIGNING_IN_MEMORY_KEY_PASSWORD)
        }

        if (propertiesNotIncluded.isNotEmpty()) {
            log(LogLevel.ERROR, "Missing properties: $propertiesNotIncluded")
            throw IllegalArgumentException("Required properties $propertiesNotIncluded not found. Please configure the required properties for the plugin to work.")
        }
    }

    private fun Project.applyRequiredPlugins() {
        plugins.apply("maven-publish")
        plugins.apply("signing")
    }
}
