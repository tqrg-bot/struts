package org.apache.struts2.sitegraph.entities;

import java.io.File;
import java.util.regex.Pattern;

/**
 */
public class FreeMarkerView extends FileBasedView {
    public FreeMarkerView(File file) {
        super(file);
    }

    protected Pattern getActionPattern() {
        return Pattern.compile("<\\@s.action [^>]*name=\"([^\"]+)\"[^>]*>");
    }

    protected Pattern getFormPattern() {
        return Pattern.compile("<\\@s.form [^>]*action=\"([^\"]+)\"[^>]*>");
    }
}
