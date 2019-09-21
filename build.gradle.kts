import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.1.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    kotlin("jvm") version "1.2.71"
    kotlin("plugin.spring") version "1.2.71"
}

group = "com.icebreaker"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-activemq")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.security.oauth", "spring-security-oauth2", "2.3.5.RELEASE")
    implementation("org.imgscalr", "imgscalr-lib", "4.2")
    implementation("com.h2database:h2")
    implementation("mysql:mysql-connector-java")
    implementation("io.projectreactor:reactor-core")
    implementation("io.netty:netty-all")
    implementation("io.projectreactor.netty:reactor-netty:0.8.9.RELEASE")

    implementation("org.webjars:webjars-locator-core")
    implementation("org.webjars:sockjs-client:1.0.2")
    implementation("org.webjars:stomp-websocket:2.3.3")
    implementation("org.webjars:bootstrap:3.3.7")
    implementation("org.webjars:jquery:3.1.0")
    implementation("net.coobird:thumbnailator:0.4.8")
    implementation("org.hashids:hashids:1.0.3")
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev103-1.25.0")
//    implementation("com.google.api-client:google-api-client:1.30.2")
    implementation("com.google.auth:google-auth-library-oauth2-http:0.17.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
