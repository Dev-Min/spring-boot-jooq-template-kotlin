import dev.bombinating.gradle.jooq.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val genDir = "$projectDir/src/main/kotlin"
val jooqDriver = "com.mysql.cj.jdbc.Driver"
val jooqUrl = "jdbc:mysql://localhost:3306/jooq_template?autoReconnect=true&serverTimezone=UTC&useSSL=false"
val jooqUserName = "root"
val jooqPassword = "root"

plugins {
    id("org.springframework.boot") version "2.3.3.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("dev.bombinating.jooq-codegen") version "1.7.0"
    id("org.flywaydb.flyway") version "6.5.5"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
//    mavenCentral()
    jcenter()
    maven {
        url = uri("https://maven.springframework.org/release")
    }
    maven {
        url = uri("https://maven.restlet.com")
    }
}

sourceSets["main"].java {
    srcDir(genDir)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.flywaydb:flyway-core:6.5.5")
    runtimeOnly("mysql:mysql-connector-java:8.0.21")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    jooqRuntime(group = "mysql", name = "mysql-connector-java", version = "8.0.21")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

jooq {
    edition = JooqEdition.OpenSource
    version = "3.13.4"
    jdbc {
        driver = jooqDriver
        url = jooqUrl
        username = jooqUserName
        password = jooqPassword
    }
    generator {
        database {
            name = "org.jooq.meta.mysql.MySQLDatabase"
            includes = ".*"
            inputSchema = "jooq_template"
        }
        generate {
            isDeprecated = false
            withInstanceFields(true)
            isPojos = false
        }
        target {
            directory = genDir
            packageName = "com.example.demo.domain"
        }
    }
}

flyway {
    url = jooqUrl
    user = jooqUserName
    password = jooqPassword
    schemas = arrayOf("jooq_template")
    locations = arrayOf("$projectDir/src/main/resources/db/migration")
}
//tasks.getByName("compileJava").dependsOn(jooq, tasks.getByName("jooq"))