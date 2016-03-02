# Usage

## build.sbt

```
val rzc = RootProject(uri("https://github.com/odwrotnie/rzepaw-commons.git#master"))
val root = Project("root", file(".")) dependsOn(rzc)
```

## build.scala

```
lazy val rzc = ProjectRef(uri("https://github.com/odwrotnie/rzepaw-commons.git#master"), "rzepawCommons")
lazy val x = Project("x", file("x"),
    ...)
    .dependsOn(rzc)
```
