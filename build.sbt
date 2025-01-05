scalaVersion := "2.13.15"

autoScalaLibrary := false

crossPaths := false

name := "javap_web"

lazy val toolsJar: File = {
  val home = file(scala.util.Properties.javaHome)
  val f1 = home / "lib" / "tools.jar"
  lazy val f2 = home.getParentFile / "lib" / "tools.jar"
  if (f1.isFile) {
    f1
  } else {
    f2
  }
}

Test / unmanagedJars += toolsJar

TaskKey[Unit]("dist") := {
  val exclude: Set[String] = Set(
  )
  val jarFiles = (Compile / fullClasspathAsJars).value.map(_.data).filterNot(f => exclude(f.getName)) :+ toolsJar
  val dir = file("sources") / "dist"
  IO.delete(dir)
  jarFiles.foreach { jar =>
    IO.copyFile(jar, dir / jar.getName)
  }
  IO.write(
    dir / "jar_files.js",
    jarFiles.map(x => s"""  "${x.getName}"""").sorted.mkString("export const jarNames = [\n", ",\n", "\n]\n")
  )
}

scalacOptions ++= Seq(
  "-deprecation",
  "-Xsource:3",
  "-release:8",
  "-Wunused:imports",
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest-freespec" % "3.2.19" % Test,
  "org.scala-sbt" %% "io" % "1.10.4" % Test,
  "org.slf4j" % "slf4j-simple" % "2.0.16" % Test,
  "ws.unfiltered" %% "unfiltered-filter" % "0.10.4" % Test,
  "ws.unfiltered" %% "unfiltered-jetty" % "0.10.4" % Test,
)

fork / run := true
run / fork := true