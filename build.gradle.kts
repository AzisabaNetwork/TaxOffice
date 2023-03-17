plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
}

group = "net.azisaba"
version = "1.1.3"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
    @Suppress("GradlePackageUpdate") // can't upgrade due to java version
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("com.github.Staartvin:Autorank-2:4.5.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.mariadb.jdbc", "net.azisaba.taxoffice.libs.org.mariadb.jdbc")
        relocate("com.zaxxer.hikari", "net.azisaba.taxoffice.libs.com.zaxxer.hikari")
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["sourcesElements"]) {
    skip()
}

publishing {
    repositories {
        maven {
            name = "repo"
            credentials(PasswordCredentials::class)
            url = uri(
                if (project.version.toString().endsWith("SNAPSHOT"))
                    project.findProperty("deploySnapshotURL") ?: System.getProperty("deploySnapshotURL", "")
                else
                    project.findProperty("deployReleasesURL") ?: System.getProperty("deployReleasesURL", "")
            )
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.getByName("sourcesJar"))
        }
    }
}
