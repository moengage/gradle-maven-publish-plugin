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

plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.plugin.gradle.publish)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.plugin.kotlin.serialization)
    `kotlin-dsl`
    `maven-publish`
    signing
}

group = project.findProperty("GROUP") as String
version = project.findProperty("VERSION_NAME") as String

gradlePlugin {
    website = "https://www.moengage.com"
    vcsUrl = "https://github.com/moengage/gradle-maven-publish-plugin/"
    plugins {
        create("publishToMavenRepositoryPlugin") {
            id = "com.moengage.plugin.maven.publish"
            implementationClass = "com.moengage.AutoPublishMavenPlugin"
            displayName = project.findProperty("NAME") as String
            description = project.findProperty("DESCRIPTION") as String
            tags = listOf("MoEngage", "Release Plugin", "MavenCentral")
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.serialization)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialisation.converter)
}

signing {
    sign(configurations.runtimeElements.get())
}
