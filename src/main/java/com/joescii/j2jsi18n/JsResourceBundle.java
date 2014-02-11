package com.joescii.j2jsi18n;

import java.util.*;

/**
 * JavaScript ResourceBundle
 */
public class JsResourceBundle {
    private final ResourceBundle bundle;

    public JsResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String toJs() {
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        for(Iterator<String> iter = bundle.keySet().iterator(); iter.hasNext();) {
            final String key = iter.next();
            final String val = bundle.getString(key);
            sb.append(key).append(':').append('"').append(val).append('"');
            if(iter.hasNext()) sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }
}
