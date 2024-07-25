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

import com.moengage.internal.AUTO_RELEASE_TASK_NAME
import com.moengage.internal.MAVEN_PUBLISH_TASK_NAME
import com.moengage.internal.MavenPublishManager
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin for publishing the android libraries to the maven central portal with auto drop & release
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Suppress("unused")
open class AutoPublishMavenPlugin : Plugin<Project> {

    private lateinit var stagedRepositoryId: String

    override fun apply(project: Project) {
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

    private fun Project.applyRequiredPlugins() {
        plugins.apply("maven-publish")
        plugins.apply("signing")
    }
}
