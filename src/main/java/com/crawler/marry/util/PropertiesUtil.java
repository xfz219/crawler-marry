package com.crawler.marry.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by finup on 2017/2/17.
 */
public class PropertiesUtil {
    private static final Logger log = Logger.getLogger(PropertiesUtil.class);
    private static Properties DEFAULT_PROPS = null;

    public static String getProps(String key) {
        return getProps(key, null);
    }

    /**
     * @param key
     * @param defVal
     * @return
     */
    public static String getProps(String key, String defVal) {
        if (DEFAULT_PROPS == null) {
            synchronized (PropertiesUtil.class) {
                if (DEFAULT_PROPS == null) {
                    DEFAULT_PROPS = readPropertiesFile("/link.properties");
                }
            }
        }
        return DEFAULT_PROPS.getProperty(key);
    }

    public static Properties readPropertiesFile(String file) {
        InputStream in = PropertiesUtil.class.getResourceAsStream(file);
        Properties prop = new Properties();
        try {
            prop.load(in);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return prop;
    }

    public static void main(String[] args) {
        System.out.println(PropertiesUtil.getProps("wed.host"));
    }
}
