#!/usr/bin/env kotlin

@file:Import("version-updater.main.kts")
@file:Import("utils.main.kts")

import java.text.SimpleDateFormat
import kotlin.system.exitProcess
import java.util.Date
import java.util.TimeZone

val publishKey = args[0]
val secretKey = args[1]
val releaseType = args[2]
val ticketNumber = args[3]

mergeDevBranchToMaster()

if (executeCommandOnShell("./gradlew assemble --stacktrace") != 0) {
    println("::error::Failed to publish plugin")
    exitProcess(1)
}

val updatedVersion = updateLibraryVersion(releaseType)

val dateFormat = SimpleDateFormat("dd-MM-YYYY")
dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"))
val date = dateFormat.format(Date())

replaceTextInFile("CHANGELOG.md", "Release Date", date)
replaceTextInFile("CHANGELOG.md", "Release Version", updatedVersion)
// commit changes
executeCommandOnShell("git add .")
executeCommandOnShell("git commit -m \"$ticketNumber : release $updatedVersion\"")
executeCommandOnShell("git push origin master")

if (executeCommandOnShell("./gradlew publishPlugins -Pgradle.publish.key=$publishKey -Pgradle.publish.secret=$secretKey") != 0) {
    println("::error::Failed to publish plugin")
    exitProcess(1)
}
tagModuleWithLatestVersion(getVersionNameForModule("plugin"))
backMergeMasterToDevelopment()