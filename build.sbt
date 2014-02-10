name := "j2js-i18n"

organization := "com.joescii"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

autoScalaLibrary := false

crossPaths := false

resolvers ++= Seq(
  "sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "sonatype-releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= {
  Seq(
    "org.scalatest"  %% "scalatest"   % "1.9.1"  % "test",
    "org.scalacheck" %% "scalacheck"  % "1.10.1" % "test"
  )
}

javacOptions in (Compile,compile) ++= Seq("-source", "1.6", "-target", "1.6", "-g")

scalacOptions <<= scalaVersion map { v: String =>
  val opts = "-deprecation" :: "-unchecked" :: Nil
  if (v.startsWith("2.9.")) opts 
  else opts ++ ("-feature" :: "-language:postfixOps" :: "-language:implicitConversions" :: Nil)
}

publishTo <<= version { _.endsWith("SNAPSHOT") match {
    case true  => Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
    case false => Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  }
}

credentials += Credentials( file("sonatype.credentials") )

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
        <url>https://github.com/barnesjd/j2js-i18n</url>
        <licenses>
            <license>
              <name>Apache 2.0 License</name>
              <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
              <distribution>repo</distribution>
            </license>
         </licenses>
         <scm>
            <url>git@github.com:barnesjd/j2js-i18n.git</url>
            <connection>scm:git:git@github.com:barnesjd/j2js-i18n.git</connection>
         </scm>
         <developers>
            <developer>
              <id>barnesjd</id>
              <name>Joe Barnes</name>
              <url>https://github.com/barnesjd</url>
            </developer>
         </developers>
 )

// Jasmine stuff
seq(jasmineSettings : _*)

// This is where our test stuff drops off the generated javascript files
appJsDir <+= (resourceManaged in Test) { _ / "js" }

// Don't really use this, but without it the plugin doesn't run.
appJsLibDir <+= (resourceManaged in Test) { _ / "js" }

// Our specs files live here.
jasmineTestDir <+= sourceDirectory { src => src / "test" / "js" }

jasmineConfFile <+= sourceDirectory { src => src / "test" / "js" / "test.dependencies.js" }

jasmineRequireJsFile <+= sourceDirectory { src => src / "test" / "js" / "3rdlib" / "require" / "require-2.0.6.js" }

jasmineRequireConfFile <+= sourceDirectory { src => src / "test" / "js" / "3rdlib" / "require.conf.js" }

(jasmine) <<= (jasmine) dependsOn (test in Test)
