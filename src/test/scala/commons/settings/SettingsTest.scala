package commons.settings

import org.scalatest.FlatSpec

/*
sbt "~rzepaw-commons/testOnly commons.settings.SettingsTest"
 */

class SettingsTest
  extends FlatSpec {

  "Properties" should "work with and without config" in {
    assert(Properties.get("app", "x").nonEmpty)
    assert(Properties.get("app", "x").get == "This is in config")
    assert(Properties.get("app", "only", "in", "no", "config").nonEmpty)
    assert(Properties.get("app", "only", "in", "config").nonEmpty)
  }
}
