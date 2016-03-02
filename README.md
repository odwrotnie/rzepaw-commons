# Usage

## build.sbt

```
val rzc = RootProject(uri(uri("https://github.com/odwrotnie/rzepaw-commons.git#master"))
val root = Project("root", file(".")) dependsOn(amn)
```

## build.scala

```
lazy val commons = ProjectRef(uri("https://github.com/odwrotnie/rzepaw-commons.git#master"), "rzepawCommons")
lazy val x = Project("x", file("x"),
    ...)
    .dependsOn(commons)
```
