package com.joescii.j2jsi18n

import org.scalatest._
import matchers.ShouldMatchers

import java.util.ResourceBundle
import java.util

class JsResourceBundleSpecs extends WordSpec with ShouldMatchers {
  "JsResourceBundle" should {
    "instantiate" in {
      val bundle = new ResourceBundle {
        override def handleGetObject(key: String): AnyRef = ???
        override def getKeys: util.Enumeration[String] = ???
      }

      val jsbundle = new JsResourceBundle(bundle)
    }
  }
}
