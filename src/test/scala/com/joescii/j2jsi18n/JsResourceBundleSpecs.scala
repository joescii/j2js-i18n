package com.joescii.j2jsi18n

import org.scalatest._
import matchers.ShouldMatchers

import java.util.ResourceBundle
import java.util

import scala.collection.JavaConverters._

object TestBundle {
  def apply(entries:(String, String)*):ResourceBundle = TestBundle(entries.toMap)
}
case class TestBundle(entries:Map[String, String]) extends ResourceBundle {
  override def handleGetObject(key: String): AnyRef = entries(key)
  override def getKeys: util.Enumeration[String] = entries.keys.iterator.asJavaEnumeration
}

/** That's right... we're testing the test code */
class TestBundleSpecs extends WordSpec with ShouldMatchers {
  "TestBundle" should {
    "implement getString()" in {
      val jbundle = TestBundle("ok" -> "OK", "cancel" -> "Cancel", "apples" -> "I have {0} apples.")

      jbundle.getString("ok") should be ("OK")
      jbundle.getString("cancel") should be ("Cancel")
      jbundle.getString("apples") should be ("I have {0} apples.")

      intercept[Exception](jbundle.getString("garbage"))
    }
  }
}

class JsResourceBundleSpecs extends WordSpec with ShouldMatchers {
  "JsResourceBundle" should {
    "instantiate" in {
      val jbundle = TestBundle("ok" -> "OK", "cancel" -> "Cancel")
      val jsbundle = new JsResourceBundle(jbundle)
    }
  }
}
