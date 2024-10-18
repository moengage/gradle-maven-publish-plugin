#!/usr/bin/env kotlin

@file:Import("./utils.kt")

import kotlin.system.exitProcess

val publishKey = args[0]
val secretKey = args[1]

mergeDevBranchToMaster()
if (executeCommandOnShell("./gradlew publishPlugins -Pgradle.publish.key=$publishKey -Pgradle.publish.secret=$secretKey") != 0) {
    println("::error::Failed to publish plugin")
    exitProcess(1)
}
tagModuleWithLatestVersion(getVersionNameForModule("plugin"))