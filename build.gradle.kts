plugins {
	java
	id("java-library")
	id("maven-publish")
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.qaroni"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

publishing {
	publications {
		create<MavenPublication>("qaroni-spring-lib") {
			group = "com.qaroni"
			artifactId = "qaroni-spring-lib"
			version = "0.0.1"
			from(components["java"])
		}
	}
}

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

tasks.withType<Test> {
	useJUnitPlatform()
}
