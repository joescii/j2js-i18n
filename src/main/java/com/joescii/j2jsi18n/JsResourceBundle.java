package com.joescii.j2jsi18n;

import java.util.ResourceBundle;

/**
 * JavaScript ResourceBundle
 */
public class JsResourceBundle {
    private final ResourceBundle bundle;

    public JsResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String toJs() {
        return "{}";
    }
}
