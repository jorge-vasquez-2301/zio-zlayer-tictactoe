val scalaVer = "2.13.6"

val attoVersion     = "0.7.2"
val zioVersion      = "1.0.10"
val zioMagicVersion = "0.3.6"

lazy val compileDependencies = Seq(
  "dev.zio"              %% "zio"       % zioVersion,
  "io.github.kitlangton" %% "zio-magic" % zioMagicVersion,
  "org.tpolecat"         %% "atto-core" % attoVersion
) map (_ % Compile)

lazy val testDependencies = Seq(
  "dev.zio" %% "zio-test"     % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion
) map (_ % Test)

lazy val settings = Seq(
  name := "zio-zlayer-tictactoe",
  version := "3.0.0",
  scalaVersion := scalaVer,
  scalacOptions += "-Ymacro-annotations",
  libraryDependencies ++= compileDependencies ++ testDependencies,
  testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
)

lazy val root = (project in file("."))
  .settings(settings)
