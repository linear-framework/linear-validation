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
  implementation(project(":macros"))

  testImplementation("junit:junit:4.13")
  testImplementation("org.scalatest:scalatest_2.13:3.1.1")
  testImplementation("org.scalatestplus:junit-4-12_2.13:3.1.1.0")
}

tasks.named<Jar>("jar") {
  from(sourceSets["main"].output)
  from(sourceSets["main"].allSource)
}

tasks.register<Copy>("copy-macro-jar") {
  dependsOn(":macros:jar")
  from("macros/build/libs/sprout-validation-macros.jar")
  into("build/libs")
}

tasks.named("build") {
  dependsOn("copy-macro-jar")
}