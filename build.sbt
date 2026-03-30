import org.goldenport.cozy.CozyPlugin.autoImport._

val scala3Version = "3.3.7"

lazy val root = project
  .in(file("."))
  .enablePlugins(org.goldenport.cozy.CozyPlugin)
  .settings(
    organization := "org.simplemodeling",
    name := "simplemodeling-model",
    version := "0.1.1-SNAPSHOT",

    scalaVersion := scala3Version,

    cozyGeneratorBackend := "cozy",
    cozyDelegateProjectDir := sys.env.get("COZY_PROJECT_DIR").map(file).orElse(Some(file("/Users/asami/src/dev2025/cozy"))),

    resolvers ++= Seq(
      Resolver.defaultLocal,
      Resolver.mavenLocal,
      "SimpleModeling.org" at "https://www.simplemodeling.org/maven"
    ),

    libraryDependencies ++= Seq(
      // Functional core
      "org.typelevel" %% "cats-core"   % "2.10.0",
      "org.typelevel" %% "cats-free"   % "2.10.0",
      "org.typelevel" %% "cats-effect" % "3.5.4",

      // JSON
      "io.circe" %% "circe-core"    % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser"  % "0.14.6",

      "org.goldenport" %% "goldenport-core" % "0.3.1-SNAPSHOT",

      // Testing
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0" % Test,
      "org.scalacheck" %% "scalacheck" % "1.17.1" % Test
    ),

    cozyManifestMetadata ++= Map(
      "component" -> "simplemodeling-model",
      "boundedContext" -> "model",
      "domain" -> "simplemodeling"
    ),

    publishMavenStyle := true,

    publishTo := {
      val repo = sys.env.get("SIMPLEMODELING_MAVEN_LOCAL")
        .map(file)
        .getOrElse(baseDirectory.value / "maven-local")

      Some(
        Resolver.file(
          "local-simplemodeling-maven",
          repo
        )
      )
    }
  )
