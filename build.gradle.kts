import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mcVersion: String by project
val yarnVersion: String by project
val loaderVersion: String by project
val fabricApiVersion: String by project
val geckoLibVersion: String by project
val languageAdapterVersion: String by project
val modVersion: String by project
val mavenGroup: String by project
val archivesBaseName: String by project

project.version = modVersion
project.group = mavenGroup

plugins {
    id("fabric-loom")
    // id("org.spaceserve.kotlin-mixins")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }

    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    }

    maven {
        name = "Kotlinx-html (Dokka)"
        url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    }
}

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:$yarnVersion:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    // Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:$languageAdapterVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.15.0") // Static analysis

    // GeckoLib
    modImplementation("software.bernie.geckolib:geckolib-fabric-1.17:$geckoLibVersion")
}

tasks {
    // Replaces "version" value in fabric.mod.json with version defined in gradle.properties
    processResources {
        inputs.property("version", project.version)

        from(sourceSets.main.get().resources.srcDirs) {
            include("fabric.mod.json")
            expand("version" to project.version)
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    // Ensures encoding is set to UTF-8, regardless of system default
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }

    java {
        withSourcesJar()
    }

    jar {
        from("LICENSE.md")
    }

    dokkaHtml {
        suppressInheritedMembers.set(true)
        suppressObviousFunctions.set(true)

        dokkaSourceSets {
            configureEach {
                includeNonPublic.set(true)
                skipEmptyPackages.set(true)
            }
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config = rootProject.files("detekt.yml")
}
