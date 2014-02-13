package com.joescii.j2jsi18n;

import java.util.*;

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

    /**
     * Returns this <code>JsResourceBundle</code> as a JavaScript object
     * @return
     */
    public String toJs() {
        // TODO: Values with arguments

        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        for(Iterator<String> iter = bundle.keySet().iterator(); iter.hasNext();) {
            final String key = iter.next();
            final String val = bundle.getString(key);
            sb.append('"').append(key).append('"')
                .append(':').append('"').append(val).append('"');
            if(iter.hasNext()) sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }
}
