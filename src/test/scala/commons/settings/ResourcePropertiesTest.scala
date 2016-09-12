package commons.settings

import org.scalatest.FlatSpec

/*
sbt "~rzepawCommons/testOnly commons.settings.ResourcePropertiesTest"
 */

class ResourcePropertiesTest
  extends FlatSpec {

  "Properties" should "work with and without config" in {
    val rp = ResourceProperties.apply("app", "properties")
    //assert(rp.get("pathconfig").nonEmpty)
    assert(rp.get("pathnoconfig").nonEmpty)
    assert(rp.get("pathnoconfig").get ==  "app.properties.pathnoconfig")
  }
}
