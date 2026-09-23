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

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${properties["minecraft_version"] as String}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"] as String}")

    // Meteor
    modImplementation("meteordevelopment:meteor-client:${properties["minecraft_version"] as String}-SNAPSHOT")

    // Baritone (Meteor's build, includes baritone.api)
    modImplementation("meteordevelopment:baritone:${properties["minecraft_version"] as String}-SNAPSHOT")
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
