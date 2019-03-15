package org.flowable.cloud.runtime.core.utils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
abstract public class HeaderUtil {
    private static final String REPLACEMENT = "-";
    private static final String ILLEGAL_CHARACTERS = "[\\t\\s\\.*#:]";
    private static final String DELIMITER = ".";
    private static final String UNDERSCORE = "_";

    public static String buildRoutingKey(Map<String, Object> headers, String... keys) {
        return Stream.of(keys)
                .map(headers::get)
                .map(Optional::ofNullable)
                .map(HeaderUtil::mapNullOrEmptyValue)
                .collect(Collectors.joining(DELIMITER));
    }

    public static String mapNullOrEmptyValue(Optional<Object> obj) {
        return obj.map(Object::toString)
                .filter(value -> !value.isEmpty())
                .map(HeaderUtil::escapeIllegalCharacters)
                .orElse(UNDERSCORE);
    }

    public static String escapeIllegalCharacters(String value) {
        return value.replaceAll(ILLEGAL_CHARACTERS, REPLACEMENT);
    }
}
