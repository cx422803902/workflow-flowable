package org.flowable.cloud.runtime.core.utils;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class NameUtil {
    /**
     * input name
     *
     * @return
     */
    public static String getInputBeanName(Class<?> clazz) {
        String connectorName = getClassName(clazz);
        return String.format("%s_%s", "cloud", connectorName);
    }

    /**
     * input handler name
     *
     * @return
     */
    public static String getMessageListenerHandler(Class<?> clazz) {
        String connectorName = getClassName(clazz);
        return String.format("%s_%s", "cloud_listener", connectorName);
    }

    /**
     * input handler name
     *
     * @return
     */
    public static String getInputHandler(Class<?> clazz) {
        String connectorName = getClassName(clazz);
        return String.format("%s_%s", "cloud_handler", connectorName);
    }

    public static String getOutputName(Class<?> clazz) {
        return getClassName(clazz);
    }
    /**
     * output name
     *
     * @return
     */
    public static String getClassName(Class<?> clazz) {
        return clazz.getCanonicalName();
    }

    public static String getJavaDelegateBeanName(String className) {
        return String.format("%s_%s", "cloud_delegate", className);
    }

}
