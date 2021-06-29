package de.mark225.bluebridge.core.util;

import org.apache.commons.text.lookup.StringLookup;

public abstract class StringLookupWrapper {

    public final StringLookup lookup = new StringLookup() {
        @Override
        public String lookup(String key) {
            return replace(key);
        }
    };

    public abstract String replace(String in);
}
