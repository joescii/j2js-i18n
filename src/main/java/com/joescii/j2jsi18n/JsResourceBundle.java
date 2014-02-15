package com.joescii.j2jsi18n;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaScript ResourceBundle
 */
public class JsResourceBundle {
    private final ResourceBundle bundle;

    /**
     * Instantiates a new <code>JsResourceBundle</code> backed by the <code>ResourceBundle</code> given.
     * @param bundle The <code>ResourceBundle</code> to back this <code>JsResourceBundle</code>.
     */
    public JsResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    private static final Pattern arg = Pattern.compile("(.*?)\\{(\\d+)\\}([^\\{]*)");

    /**
     * Returns this <code>JsResourceBundle</code> as a JavaScript object
     * @return
     */
    public String toJs() {
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        for(Iterator<String> iter = bundle.keySet().iterator(); iter.hasNext();) {
            final String key = iter.next();
            final String val = bundle.getString(key);

            sb.append('"').append(key).append('"').append(':');

            Matcher m = arg.matcher(val);
            if(m.find()) {
                List<String> l = new LinkedList<String>();
                l.add(m.group(1));

                // Get all of the chunks of the value
                do {
                    l.add(m.group(3));
                } while (m.find());

                // Build the function declaration
                sb.append("function(");
                for(int i=0; i<l.size()-1; i++) {
                    sb.append("p").append(i);
                    if(i < l.size()-2) // this is NOT the last one
                      sb.append(",");
                }
                sb.append("){return'");

                // Assemble the string and arguments for the body of the function
                sb.append(l.get(0)).append("'"); // Add the leading stuff
                for(int i=1; i<l.size(); i++)
                    sb.append("+p"+(i-1)+"+'").append(l.get(i)).append("'");
                sb.append(";}");
            }
            else {
                sb.append('"').append(val).append('"');
            }

            if(iter.hasNext()) sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }
}
