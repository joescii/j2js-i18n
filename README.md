# j2js-i18n

This little Java project converts java.util.ResourceBundle/java.text.MessageFormat into JavaScript.  

## Problem Statement

If you're writing a JVM-based web application, there is a great chance you are utilizing Java's property file/resource bundle/message formate approach to i18n.  While this is very useful on the server side, i18n is also often needed for JavaScript-based client code.  For projects without complex internationalization needs, it may be most convenient in some cases to simply reuse those same Java resource bundles in the JavaScript client.  While it is true that [jquery-i18n-properties](https://code.google.com/p/jquery-i18n-properties/) already exists to fulfil this need, it is a jQuery plugin.  Not all of us use jQuery.  

## The j2js-i18n Approach to a Solution

This library not meant to be a full solution, but an 80/20 solution which will work for most cases.  The production code is 100% pure Java with zero dependencies, making it suitable for inclusion in any JVM-based web application.  The Scala and JavaScript source are only utilized for testing purposes.  

## Configuration

This project is available as a Maven artifact at Sonatype.org.  

### Maven

Add **j2js-i18n** as a dependency in your `maven.pom`.

```xml
<project>
  <!-- ... -->
  <dependencies>
    <dependency>
      <groupId>com.joescii</groupId>
      <artifactId>j2js-i18n</artifactId>
      <version>0.1</version>
    </dependency>
  <!-- ... -->
  </dependencies>
</project>
```

### Scala sbt
Add the Sonatype.org Releases repo as a resolver in your `build.sbt` or `Build.scala` as appropriate.

```scala
resolvers += "Sonatype.org Releases" at "https://oss.sonatype.org/content/repositories/releases/"
```

Add **j2js-i18n** as a dependency in your `build.sbt` or `Build.scala` as appropriate.

```scala
libraryDependencies ++= Seq(
  // Other dependencies ...
  "com.joescii" % "j2js-i18n" % "0.1.1" % "compile"
)
```

## Usage

To use **j2js-i18n**, you instantiate a `com.joescii.j2jsi18n.JsResourceBundle` passing it the `java.util.ResourceBundle` that is appropriate for the request's locale (here we assume your web application framework is already doing a great job of choosing the appropriate bundles).  Call the `toJs()` method to access the JavaScript object.

## Javadocs

The latest javadocs can be found [here](http://joescii.github.io/j2js-i18n/api/), although there is very little to see.

## Examples

Below are some examples of utilizing **j2js-i18n** in different languages and frameworks.

### Java

TBD

### Scala Lift

```scala
package org.example.snippet

import net.liftweb.http.S._
import com.joescii.j2jsi18n._

case object MySnip {
  def render = resourceBundles.headOption.map { bundle =>
    val jsObj = new JsResourceBundle(bundle).toJs
    <script type="text/javascript">
      { s"i18n = $jsObj" }
    </script>
  }
}
```

## Change log

* *0.1.1*: Bug fix to correctly escape quotes in parameterized strings
* *0.1*: Initial release

