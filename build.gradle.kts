
plugins {
	java
}

group = "edu.duke.cs"

repositories {
	jcenter()
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {

	// test dependencies
	testCompile("org.hamcrest:hamcrest-all:1.3")
	testCompile("junit:junit:4.12")

	implementation("org.apache.logging.log4j:log4j-core:2.13.1")
	implementation("org.apache.logging.log4j:log4j-api:2.13.1")
	implementation("gov.nist.math:jama:1.0.3")
	implementation("org.jfree:jfreechart:1.0.19")
	implementation("org.apache.xmlgraphics:xmlgraphics-commons:2.4")
	implementation("batik:batik-xml:1.6-1")
	implementation("batik:batik-css:1.6-1")
	implementation("batik:batik-dom:1.6-1")
	implementation("batik:batik-ext:1.6-1")
	implementation("batik:batik-svggen:1.6-1")
	implementation("batik:batik-util:1.6-1")
	implementation("xerces:xercesImpl:2.12.0")
}
