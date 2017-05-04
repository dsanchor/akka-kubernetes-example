import com.typesafe.sbt.packager.docker._

name := "HelloWorld"
version := "1.0"
scalaVersion := "2.11.7"

val akkaVersion = "2.4.16"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

/* dependencies */
libraryDependencies ++= Seq (
  // -- Akka --
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
  ,"com.typesafe.akka" %% "akka-cluster" % akkaVersion
)

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

mainClass in Compile := Some("com.redhat.services.akka.HelloWorldMain")

dockerCommands := Seq(
  Cmd("FROM", "redhat-openjdk-18/openjdk18-openshift"),
  Cmd("WORKDIR", "/opt/docker"),
  ExecCmd("ADD", "opt", "/opt"),
  Cmd("USER", "root"),
  ExecCmd("RUN", "chown", "-R", "daemon:daemon", "."),
  ExecCmd("RUN", "chmod", "777", "/opt/docker"),
  Cmd("USER", "daemon"),
  ExecCmd("ENTRYPOINT", "bin/helloworld")
)
