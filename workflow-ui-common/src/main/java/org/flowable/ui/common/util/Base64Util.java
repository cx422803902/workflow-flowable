package org.flowable.ui.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.util.StringUtils;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class Base64Util {
    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);
    protected static final String DELIMITER = ":";

    public static String[] decode(String value) throws InvalidCookieException {
        for (int j = 0; j < value.length() % 4; j++) {
            value = value + "=";
        }

        String cookieAsPlainText = null;
        try {
            cookieAsPlainText = new String(Base64.getDecoder().decode(value.getBytes()));
        }
        catch (IllegalArgumentException e) {
            throw new InvalidCookieException("Cookie token was not Base64 encoded; value was '" + value + "'");
        }

        String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText, DELIMITER);

        for (int i = 0; i < tokens.length; i++) {
            try {
                tokens[i] = URLDecoder.decode(tokens[i], StandardCharsets.UTF_8.toString());
            }
            catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return tokens;
    }

    public static String encode(String[] tokens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            try {
                sb.append(URLEncoder.encode(tokens[i], StandardCharsets.UTF_8.toString()));
            }
            catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }

            if (i < tokens.length - 1) {
                sb.append(DELIMITER);
            }
        }

        String value = sb.toString();

        sb = new StringBuilder(new String(Base64.getEncoder().encode(value.getBytes())));

        while (sb.charAt(sb.length() - 1) == '=') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
