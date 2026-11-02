plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    // Vault
    maven("https://jitpack.io")
    // dynmap
    maven("https://repo.mikeprimm.com/")
    // Multiverse-Core
    maven("https://repo.onarandombox.com/content/groups/public/")
    // mcMMO
    maven("https://nexus.neetgames.com/repository/maven-public")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    // 連携するプラグイン(どれも softdepend。無くても動く)
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") {
        isTransitive = false
    }
    compileOnly("us.dynmap:dynmap-api:2.5") {
        isTransitive = false
    }
    compileOnly("org.mvplugins.multiverse.core:multiverse-core:5.2.1") {
        isTransitive = false
    }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.041-SNAPSHOT") {
        isTransitive = false
    }

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    processResources {
        // Maven のときと同じく、${project.name} などをビルド時に置き換える
        val name = project.name
        val version = project.version.toString()
        val description = project.description.toString()
        inputs.property("version", version)
        inputs.property("description", description)
        filteringCharset = "UTF-8"
        filesMatching(listOf("plugin.yml", "config.yml", "config_en.yml", "messages_*.yml")) {
            filter { line ->
                line.replace("\${project.name}", name)
                    .replace("\${project.version}", version)
                    .replace("\${project.description}", description)
            }
        }
    }

    jar {
        // LGPL v3 の条文(LICENSE)と、その元になる GPL v3 の条文(COPYING)を jar に入れる
        from(files("LICENSE", "COPYING")) {
            into("META-INF")
        }
    }

    test {
        useJUnit()
        jvmArgs("-Dfile.encoding=UTF-8")
    }
}
