package com.zuehlke.securesoftwaredevelopment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PlainTextPasswordEncoder implements PasswordEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(PlainTextPasswordEncoder.class);

    @Override
    public String encode(CharSequence charSequence) {
        String retVal = null;
        if (charSequence != null) {
            retVal = charSequence.toString();
        } else {
            LOG.error("Char sequence is null");
        }
        return retVal;
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        boolean isMatch = false;
        if (charSequence != null && s != null) {
            isMatch = charSequence.toString().equals(s);
        } else {
            LOG.error("Char sequence or string is null");
        }
        return isMatch;
    }
}
