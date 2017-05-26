package com.joescii.j2jsi18n;

/**
 * Contains snippets of JsCode to ease configuring j2js-i18n.
 */
public class JsCode {
    /**
     * Creates a logMiss() function, providing variables <code>id</code> (the localization ID that missed),
     * <code>deflt</code> (the default value), and <code>args</code> (an array containing the args passed)
     * @param body string containing lines of JavaScript for handling a localization miss.
     * @return string containing a JS function for passing to <code>JsResourceBundle.toJs()</code>
     */
    public static String logMiss(String body) { return
        "function(id,deflt){" +
            "var args=3<=arguments.length?[].slice.call(arguments,2):[];" +
            body +
        "}";
    }

    /**
     * Series of JavaScript statements that builds <code>argStr</code>, giving a comma-delimited list of the
     * arguments passed during a localization miss.
     */
    public static String argStr =
        "var argStr=\"(\";" +
        "for(i=0;i<args.length;i++){" +
            "argStr+=args[i];" +
            "if(i+1<args.length)argStr+=\", \";" +
        "}" +
        "argStr+=\")\";";

    /**
     * A good-enough implementation of the <code>logMiss</code> function which will print to the console the
     * ID that was missed, and the args provided at call time.
     */
    public static String logMissWithArgs = logMiss("" +
        argStr +
        "console.log(\"Resource bundle did not contain id '\"+id+\"'. Args were \"+argStr+\".\");"
    );
}
