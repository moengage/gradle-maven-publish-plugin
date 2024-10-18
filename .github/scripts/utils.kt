import java.io.File
import java.io.FileInputStream
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