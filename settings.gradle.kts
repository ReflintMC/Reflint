import java.util.Locale

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

if (!file(".git").exists()) {
    val errorText = """
        
        =====================[ ERROR ]=====================
         The Reflint project directory is not a properly cloned Git repository.
         
         In order to build Reflint from source you must clone
         the Paper repository using Git, not download a code
         zip from GitHub.
         
         Built Paper jars are available for download at
         https://reflintmc.github.io
         
         See https://github.com/ReflintMC/Reflint
         for further information on building and modifying Reflint.
        ===================================================
    """.trimIndent()
    error(errorText)
}

rootProject.name = "reflint"

for (name in listOf("paper-api", "paper-server")) {
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    file(name).mkdirs()
    findProject(":$projName")!!.projectDir = file(name)
}

optionalInclude("test-plugin")
optionalInclude("paper-api-generator")

fun optionalInclude(name: String, op: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("$name.settings.gradle.kts")
    if (settingsFile.exists()) {
        apply(from = settingsFile)
        findProject(":$name")?.let { op?.invoke(it) }
    } else {
        settingsFile.writeText(
            """
            // Uncomment to enable the '$name' project
            // include(":$name")

            """.trimIndent()
        )
    }
}
