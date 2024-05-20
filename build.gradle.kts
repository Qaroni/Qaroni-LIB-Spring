plugins {
	java
	id("java-library")
	id("maven-publish")
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.qaroni"
version = "0.0.1"
description = "Spring boot library for Qaroni projects"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-test")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")
	implementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.1.2")

	testImplementation("io.rest-assured:rest-assured:5.4.0")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	withSourcesJar()
}

afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>("mavenJava") {
				from(components["java"])
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.wrapper {
	gradleVersion = "8.7"
}