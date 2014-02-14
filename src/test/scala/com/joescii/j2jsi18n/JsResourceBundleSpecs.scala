package com.joescii.j2jsi18n

import org.scalatest._
import matchers.ShouldMatchers

import java.util.ResourceBundle
import java.util
import java.io._

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
  lazy val dir = new File(System.getProperty("com.joescii.test.js"))

  def write(name:String, contents:String) = {
    val file = new File(dir, name)
    val writer = new PrintStream(new FileOutputStream(file))
    writer.println(contents)
    writer.close()
  }

  def generate(m:Map[String, String], i:Int) = {
    val js = new JsResourceBundle(TestBundle(m)).toJs
    write(s"test$i.js", s"var test$i = $js;")
  }

  "JsResourceBundle" should {
    "generate test0.js" in {
      generate(Map(), 0)
    }

    "generate test1.js" in {
      generate(Map("ok" -> "OK", "cancel" -> "Cancel"), 1)
    }

    "generate test2.js" in {
      generate(Map("class" -> "Clazz", "com.joescii" -> "Joe Barnes"), 2)
    }

    "generate test3.js" in {
      generate(Map("params" -> "This has {0} param(s)."), 3)
    }
  }
}
