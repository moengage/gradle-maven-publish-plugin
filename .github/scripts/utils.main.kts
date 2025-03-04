import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

/**
 * Executes the provided command in working directory on the bash shell.
 */
fun executeCommandOnShell(command: String): Int {
    val process = ProcessBuilder("/bin/bash", "-c", command).inheritIO().start()
    return process.waitFor()
}

/**
 * Merge development branch to master branch
 */
fun mergeDevBranchToMaster() {
    executeCommandOnShell("git checkout master")
    executeCommandOnShell("git pull origin master")
    executeCommandOnShell("git checkout development")
    executeCommandOnShell("git pull origin development")
    executeCommandOnShell("git merge master")
    executeCommandOnShell("git checkout master")
    executeCommandOnShell("git merge development")
    executeCommandOnShell("git push origin master")
}

/**
 * Create and push the git tags for single module project
 */
fun tagModuleWithLatestVersion(version: String) {
    val tag = "v$version"
    println("version: $version \ntag: $tag")
    val command = "git tag -a $tag -m \"release $version\""
    executeCommandOnShell(command)
    executeCommandOnShell("git push origin --tags")
}

/**
 * Fetch the value for VERSION_NAME from gradle.properties.
 */
@Throws(IllegalStateException::class)
fun getVersionNameForModule(module: String): String {
    val properties = Properties()
    properties.load(FileInputStream("./$module/gradle.properties"))
    return properties.getProperty("VERSION_NAME") ?: run {
        println("VERSION_NAME not found")
        throw IllegalStateException("VERSION_NAME not found")
    }
}

fun replaceTextInFile(filePath: String, currentText: String, updatedText: String) {
    val file = File(filePath)
    if (!file.exists()) {
        throw IllegalStateException("File does not exist")
    }
    val fileContent = file.readText()
    val updatedFileContent = fileContent.replaceFirst(currentText, updatedText)
    file.writeText(updatedFileContent)
}

fun readProperties(filePath: String): Properties {
    val properties = Properties()
    properties.load(FileInputStream(filePath))
    return properties
}

fun writeUpdatedVersion(filePath: String, properties: Properties, key: String, value: String) {
    properties.setProperty(key, value)
    properties.store(FileOutputStream(filePath), "$value version update")
}