package com.sfu.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropsUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(PropsUtil.class);

    public static Properties loadProps(String filename) {
        Properties props = null;
        InputStream is = null;
        try{
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            if (is == null){
                throw new FileNotFoundException(filename+"file is not found");
            }
            props = new Properties();
            props.load(is);
        }catch (Exception e){
            LOGGER.error("load properties file failure",e);
        }finally {
            if (is!=null){
                try{
                    is.close();
                }catch (IOException e){
                    LOGGER.error("close input stream failure",e);
                }
            }
        }
        return props;

    }

    public static String getString(Properties props, String key){
        return getString(props, key,"");
    }

    public static String getString(Properties props,String key,String defaultValue){
        return props.getProperty(key,defaultValue);
    }

    public static int getInt(Properties props, String key){
        return getInt(props,key,0);
    }
    public static int getInt(Properties props, String key, int defaultValue){
        return Integer.parseInt(props.getProperty(key,String.valueOf(defaultValue)));
    }
    public static boolean getBoolean(Properties props, String key){
        return getBoolean(props,key,false);
    }
    public static boolean getBoolean(Properties props, String key, boolean defaultValue){
        return props.getProperty(key,"false").equalsIgnoreCase("true");
    }
}
