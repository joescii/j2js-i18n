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
      generate(Map(
        "class" -> "Clazz",
        "com.joescii" -> "Joe Barnes",
        "a 'key'" -> "a 'value'",
        "with.newline" -> "another \n newline",
        "dbl\"quote" -> "ridiculous, but should work"
      ), 2)
    }

    "generate test3.js" in {
      generate(Map(
        "params" -> "This has {0} param(s).",
        "leading" -> "{0} param(s) in the lead.",
        "trailing" -> "Number of params trailing is {0}"
      ), 3)
    }

    "generate test4.js" in {
      generate(Map(
        "params2" -> "This {0} has {1} two params.",
        "params3" -> "This {0} has {1} three {2} params.",
        "params9" -> "Just {0} for {1} really {2} good {3} measure {4} this {5} string {6} has {7} nine! {8}"
      ), 4)
    }

    "generate test5.js" in {
      generate(Map(
        "order2" -> "Out {1} of {0} order",
        "order3" -> "Out {1} of {0} order {2}",
        "missing2" -> "Missing {2} one {0}"
      ), 5)
    }
  }
}

import java.text.MessageFormat
import org.mozilla.javascript._
import org.scalacheck._

object JsResourceBundleChecks extends Properties("JsResourceBundle") {
  def jsCheck(js:String, expected:String):Boolean = {
    val cx = Context.enter()
    try {
      val scope = cx.initStandardObjects()
      val res = cx.evaluateString(scope, js, "line", 1, null)
      val vjs = scope.get("v", scope)
      Context.toString(vjs) == expected
    } catch {
      case e:Exception => {
        println(e.toString+": "+js)
        false
      }
    } finally {
      Context.exit()
    }
  }

  def isLegalJsString(s:String):Boolean = {
    val cx = Context.enter()
    try {
      val scope = cx.initStandardObjects()
      val res = cx.evaluateString(scope, s"v = '$s'", "line", 1, null)
      val vjs = scope.get("v", scope)
      Context.toString(vjs) == s
    } catch {
      case e:Exception => false
    } finally {
      Context.exit()
    }
  }

  import Prop._

  property("toJs(no param values)") = forAll { (k:String, v:String) =>
    (!k.isEmpty && isLegalJsString(k) && isLegalJsString(v)) ==> {
      val i18n = new JsResourceBundle(TestBundle(k -> v)).toJs
      jsCheck(s"i18n = $i18n; v = i18n['$k'];", v)
    }
  }

  property("toJs(1 param)") = forAll { (k:String, v:String, pos:Int, p0:String) =>
    (!k.isEmpty && isLegalJsString(k) && isLegalJsString(v) && isLegalJsString(p0)) ==> {
      val vSplit = v.splitAt(if(v.length > 0) pos % v.length else 0)
      val vWithParam = vSplit._1 + "{0}" + vSplit._2
      val i18n = new JsResourceBundle(TestBundle(k -> vWithParam)).toJs
      val formatted = MessageFormat.format(vWithParam, p0)
      jsCheck(s"i18n = $i18n; v = i18n['$k']('$p0');", formatted)
    }
  }
}