# j2js-i18n

This little Java project converts java.util.ResourceBundle/java.text.MessageFormat into JavaScript.  JVM-based projects which utilize the Java approach to i18n on the server can use this as a utility to make the i18n resources available on the JavaScript client.  I often hear of folks writing their own quick-and-dirty library for this, so I thought it was time to make an open-source one we can all use.  It is not meant to be a full solution, but an 80/20 solution which will work for most cases.  
