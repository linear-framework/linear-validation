plugins {
  `java-library`
  scala
}

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("org.scala-lang:scala-library:2.13.1")
  implementation("org.scala-lang:scala-reflect:2.13.1")
}

tasks.named<Jar>("jar") {
  archiveBaseName.set("linear-validation-macros")
  from(sourceSets["main"].output)
  from(sourceSets["main"].allSource)
}