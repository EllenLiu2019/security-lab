package com.ellen.security.lab.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceUtil {

    public static final String FILE_PREFIX = "file:";
    public static final String CLASSPATH_PREFIX = "classpath:";
    public static final String SLASH = "/";

    public static InputStream getResourceAsStream(String resource) throws FileNotFoundException {
        return resource.startsWith(CLASSPATH_PREFIX) ?
                ResourceUtil.class.getClassLoader().getResourceAsStream(getClasspathResource(resource)) :
                new FileInputStream(ResourceUtils.getFile(resource));
    }

    public static String getClasspathResource(String url) {
        if (Strings.CI.startsWith(url, CLASSPATH_PREFIX)) {
            url = Strings.CI.removeStart(url, CLASSPATH_PREFIX);
        }

        return Strings.CS.startsWith(url, SLASH) ? Strings.CS.removeStart(url, SLASH) : url;
    }

    public static String getEnvOrProperty(String name) {
        String value = System.getenv(name);
        return StringUtils.isBlank(value) ? System.getProperty(name) : value;
    }
}
