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

            // TODO: Generalize for arbitrary number of args.
            Matcher m = arg.matcher(val);
            if(m.find())
                sb.append("function(p0){return'").append(m.group(1)).append("'+p0+'").append(m.group(3)).append("';}");
            else
                sb.append('"').append(val).append('"');

            if(iter.hasNext()) sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }
}
