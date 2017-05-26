package com.joescii.j2jsi18n

import org.scalatest._
import java.util.ResourceBundle
import java.util
import java.io._

import org.mozilla.javascript.Context

import scala.collection.JavaConverters._

object TestBundle {
  def apply(entries:(String, String)*):ResourceBundle = TestBundle(entries.toMap)
}
case class TestBundle(entries:Map[String, String]) extends ResourceBundle {
  override def handleGetObject(key: String): AnyRef = entries(key)
  override def getKeys: util.Enumeration[String] = entries.keys.iterator.asJavaEnumeration
}

/** That's right... we're testing the test code */
class TestBundleSpecs extends WordSpec with Matchers {
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

object JsEngine {
  def returnv(js:String): Option[String] = {
    val cx = Context.enter()
    try {
      val scope = cx.initStandardObjects()
      val res = cx.evaluateString(scope, js, "line", 1, null)
      val vjs = scope.get("v", scope)
      Some(Context.toString(vjs))
    } catch {
      case e:Exception => {
        println(e.toString+": "+js)
        None
      }
    } finally {
      Context.exit()
    }
  }
}

class JsResourceBundleSpecs extends WordSpec with Matchers {
  lazy val dir = new File(System.getProperty("com.joescii.test.js"))

  def write(name:String, contents:String) = {
    val file = new File(dir, name)
    val writer = new PrintStream(new FileOutputStream(file))
    writer.println(contents)
    writer.close()
  }

  def runChecksNoParams(m:Map[String, String], i:Int): Unit = {
    val js = new JsResourceBundle(TestBundle(m)).toJs
    write(s"test$i.js", s"var test$i = $js;")
    m.foreach { case (k, v) =>
      val result = JsEngine.returnv(s"""i18n = $js; v = i18n["$k"]();""")
      result.isDefined shouldEqual true
      result.map(_ shouldEqual v)
    }
  }

  def runChecksParams(m: Map[String, (String, String, String)], i: Int): Unit = {
    val input = m.mapValues(_._1)
    val js = new JsResourceBundle(TestBundle(input)).toJs
    write(s"test$i.js", s"var test$i = $js;")
    m.foreach { case (k, (_, args, expected)) =>
      val result = JsEngine.returnv(s"""i18n = $js; v = i18n["$k"]($args);""")
      result.isDefined shouldEqual true
      result.map(_ shouldEqual expected)
    }
  }

  def runCheckV(m: Map[String, (String, String, String)], i: Int): Unit = {
    val input = m.mapValues(_._1)
    val js = new JsResourceBundle(TestBundle(input)).toJs("null") // Need to kill logging since console isn't defined
    write(s"test$i.js", s"var test$i = $js;")
    m.foreach { case (k, (_, v, expected)) =>
      val result = JsEngine.returnv(s"""i18n = $js; v = $v;""")
      result.isDefined shouldEqual true
      result.map(_ shouldEqual expected)
    }
  }

  "JsResourceBundle" should {
    "handle an empty bundle" in {
      runChecksNoParams(Map(), 0)
    }

    "handle trivial values" in {
      runChecksNoParams(Map("ok" -> "OK", "cancel" -> "Cancel"), 1)
    }

    "handle values with quotes, etc" in {
      runChecksNoParams(Map(
        "class" -> "Clazz",
        "com.joescii" -> "Joe Barnes",
        "a 'key'" -> "a 'value'",
        "with.newline" -> "another \n newline",
        "dblquote" -> "ridiculous, \" but should work",
        "backslash" -> "Back\\slash"
      ), 2)
    }

    "handle single parameters" in {
      runChecksParams(Map(
        "params" -> ("This has {0} param(s).", "'arg0'", "This has arg0 param(s)."),
        "leading" -> ("{0} param(s) in the lead.", "'arg0'", "arg0 param(s) in the lead."),
        "trailing" -> ("Number of params trailing is {0}", "'arg0'", "Number of params trailing is arg0")
      ), 3)
    }

    "handle multiple parameters" in {
      runChecksParams(Map(
        "params2" -> ("This {0} has {1} two params.", "'arg0','arg1'", "This arg0 has arg1 two params."),
        "params3" -> ("This {0} has {1} three {2} params.", "'arg0','arg1','arg2'", "This arg0 has arg1 three arg2 params."),
        "params9" -> ("Just {0} for {1} really {2} good {3} measure {4} this {5} string {6} has {7} nine! {8}", "'a0','a1','a2','a3','a4','a5','a6','a7','a8'",
          "Just a0 for a1 really a2 good a3 measure a4 this a5 string a6 has a7 nine! a8")
      ), 4)
    }

    "handle out-of-order parameters" in {
      runChecksParams(Map(
        "order2" -> ("Out {1} of {0} order", "'arg0','arg1'", "Out arg1 of arg0 order"),
        "order3" -> ("Out {1} of {0} order {2}", "'arg0','arg1','arg2'", "Out arg1 of arg0 order arg2"),
        "missing2" -> ("Missing {2} one {0}", "'arg0','arg1','arg2'", "Missing arg2 one arg0")
      ), 5)
    }

    "handle parameterized strings with quotes" in {
      runChecksParams(Map(
        "contains.quotes" -> ("This \" has a {0} quote", "'arg0'", "This \" has a arg0 quote"),
        "arg.contains.quote" -> ("This has {0} no quotes", "'ar\"g'", "This has ar\"g no quotes"),
        "both.contain.quotes" -> ("These \" both have a {0} quote", "'ar\"g'", "These \" both have a ar\"g quote"),
        "single.quote" -> ("a ' {0} single", "'arg0'", "a ' arg0 single")
      ), 6)
    }

    "have a safe accessor" in {
      runCheckV(Map(
        "no.params" -> ("no params", "i18n.localize('no.params', 'default')", "no params"),
        "undefined" -> ("ignored", "i18n.localize('garbage', 'default')", "default"),
        "one.arg" -> ("one {0} param", "i18n.localize('one.arg', 'default', 'arg0')", "one arg0 param"),
        "two.args" -> ("{1} two {0} params", "i18n.localize('two.args', 'default', 'arg0', 'arg1')", "arg1 two arg0 params")
      ), 7)
    }
  }
}

import java.text.MessageFormat
import org.mozilla.javascript._
import org.scalacheck._

object JsResourceBundleChecks extends Properties("JsResourceBundle") {
  import Prop._

  def jsCheckBoolean(js: String, expected: String): Boolean =
    JsEngine.returnv(js).map(expected == _ ).getOrElse(false)

  property("toJs(no param values)") = forAll(Gen.identifier, Gen.identifier) { (k:String, v:String) =>
    (!k.isEmpty) ==> {
      val i18n = new JsResourceBundle(TestBundle(k -> v)).toJs
      jsCheckBoolean(s"i18n = $i18n; v = i18n['$k']();", v)
    }
  }

  property("toJs(1 param)") = forAll(Gen.identifier, Gen.identifier, Gen.choose(1, 1000), Gen.identifier) { (k:String, v:String, pos:Int, p0:String) =>
    (!k.isEmpty) ==> {
      val vSplit = v.splitAt(if(v.length > 0) pos % v.length else 0)
      val vWithParam = vSplit._1 + "{0}" + vSplit._2
      val i18n = new JsResourceBundle(TestBundle(k -> vWithParam)).toJs
      val formatted = MessageFormat.format(vWithParam, p0)
      if(jsCheckBoolean(s"i18n = $i18n; v = i18n['$k']('$p0');", formatted))
        true
      else {
        println("k:  "+k.getBytes.mkString(","))
        println("v:  "+v.getBytes.mkString(","))
        println("p0: "+p0.getBytes.mkString(","))
        false
      }
    }
  }
}