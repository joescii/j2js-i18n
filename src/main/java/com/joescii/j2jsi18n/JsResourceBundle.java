package com.joescii.j2jsi18n;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaScript ResourceBundle
 */
public class JsResourceBundle {
    /**
     * Instantiates a new <code>JsResourceBundle</code> backed by the <code>ResourceBundle</code> given.
     * @param bundle The <code>ResourceBundle</code> to back this <code>JsResourceBundle</code>.
     */
    public JsResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Returns this <code>JsResourceBundle</code> as a JavaScript object.
     * Defaults <code>logMiss</code> to <code>JsCode.logMissWithArgs</code>.
     * @return a string-encoded JS object.
     */
    public String toJs() {
        return toJs(JsCode.logMissWithArgs);
    }

    /**
     * Returns this <code>JsResourceBundle</code> as a JavaScript object.
     * Any localization key/id misses are passed to the <code>logMissFn</code>
     * @param logMissFn javascript callback function called when a localization miss occurs (@seeAlso JsCode).
     * @return a string-encoded JS object.
     */
    public String toJs(String logMissFn) {
        final StringBuilder sb = new StringBuilder();
        String logMissFnClean = logMissFn.trim();
        if(logMissFnClean.endsWith(";")) logMissFnClean = logMissFnClean.substring(0, logMissFnClean.length() - 1);
        sb.append('{');
        sb.append("logMiss:" + logMissFnClean + ",");
        sb.append(localizeJsFn);
        for(Iterator<String> iter = bundle.keySet().iterator(); iter.hasNext();) {
            final String key = iter.next();
            final String val = bundle.getString(key);

            sb.append('"').append(legalize(key)).append('"').append(':');

            Matcher m = arg.matcher(val);
            if(m.find()) {
                List<String> chunks = new LinkedList<String>();
                List<Integer> args = new ArrayList<Integer>();
                chunks.add(m.group(1));

                // Get all of the chunks of the value
                do {
                    args.add(Integer.parseInt(m.group(2)));
                    chunks.add(m.group(3));
                } while (m.find());

                // Build the function declaration
                int numArgs = Collections.max(args);
                sb.append("function(");
                for(int i=0; i<=numArgs; i++) {
                    sb.append("p").append(i);
                    if(i < numArgs) // this is NOT the last one
                      sb.append(",");
                }
                sb.append("){return\"");

                // Assemble the string and arguments for the body of the function
                sb.append(legalize(chunks.get(0))).append('"'); // Add the leading stuff
                for(int i=1; i<chunks.size(); i++)
                    sb.append("+p"+args.get(i-1)+"+\"").append(legalize(chunks.get(i))).append('"');
                sb.append(";}");
            }
            else {
                sb.append("function(){return");
                sb.append('"').append(legalize(val)).append('"');
                sb.append(";}");
            }

            sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }
    
    private final ResourceBundle bundle;

    private static final Pattern arg = Pattern.compile("(.*?)\\{(\\d+)\\}([^\\{]*)");

    private String legalize(String s) {
        return s.replaceAll("\\\\", Matcher.quoteReplacement("\\\\"))
                .replaceAll("\"", Matcher.quoteReplacement("\\\""))
                .replaceAll("\\n", "\\\\n")
                ;
    }

    private String localizeJsFn =
        "localize:function(i,d){" +
            "var f=this[i];" +
            "var as=3<=arguments.length?[].slice.call(arguments,2):[];" +
            "if(typeof f==='function')" +
                "return f.apply(this,as);" +
            "else if(typeof this.logMiss==='function'){" +
                "this.logMiss.apply(this,[].slice.call(arguments));" +
                "return d;" +
            "}" +
            "else return d;"+
        "},";

}
