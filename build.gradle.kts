import java.util.zip.ZipFile

plugins {
    id("fabric-loom") version "1.17-SNAPSHOT"
}

base {
    archivesName = properties["archives_base_name"] as String
    version = properties["mod_version"] as String
    group = properties["maven_group"] as String
}

repositories {
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
    maven {
        name = "babbaj"
        url = uri("https://babbaj.github.io/maven/")
    }
}

// DBK's Baritone fork (../baritone, branch dbk-airplace) for runClient, if it has been built there (./gradlew :fabric:build).
// Picks the newest unoptimized jar built for our Minecraft version; other branches of the fork build into the same folder.
val baritoneFork: File? = file("../baritone/fabric/build/libs")
    .listFiles { f -> f.name.startsWith("baritone-unoptimized-fabric-") && f.name.endsWith(".jar") }
    ?.filter { jar ->
        val zip = ZipFile(jar)
        try {
            val entry = zip.getEntry("fabric.mod.json")
            entry != null && zip.getInputStream(entry).reader().readText()
                .contains("\"${properties["minecraft_version"] as String}\"")
        } finally {
            zip.close()
        }
    }
    ?.maxByOrNull { it.lastModified() }

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${properties["minecraft_version"] as String}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"] as String}")

    // Meteor
    modImplementation("meteordevelopment:meteor-client:${properties["minecraft_version"] as String}-SNAPSHOT")

    // Baritone (Meteor's build, includes baritone.api). Compiled against in any case; at runtime the fork replaces it when present
    if (baritoneFork != null) {
        modCompileOnly("meteordevelopment:baritone:${properties["minecraft_version"] as String}-SNAPSHOT")
        modLocalRuntime(files(baritoneFork))
    } else {
        modImplementation("meteordevelopment:baritone:${properties["minecraft_version"] as String}-SNAPSHOT")
    }
    // Nested inside the Baritone jar but not declared in its pom, so the dev client can't find it without this
    runtimeOnly("dev.babbaj:nether-pathfinder:1.4.1")
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "mc_version" to project.property("minecraft_version"),
        )

        inputs.properties(propertyMap)

        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    jar {
        val licenseSuffix = project.base.archivesName.get()
        from("LICENSE") {
            rename { "${it}_${licenseSuffix}" }
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
    }
}
