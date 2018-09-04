package com.sfu.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public final class ClassUtil {
    private static final Logger lOGGER = LoggerFactory.getLogger(ClassUtil.class);
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String className, boolean isInitialized){
        Class<?> cls;
        try{
            cls = Class.forName(className, isInitialized,getClassLoader());
        }catch(ClassNotFoundException ex){
            lOGGER.error("load class failure",ex );
            throw new RuntimeException(ex);
        }

        return cls;
    }

    public static Set<Class<?>> getClassSet(String packageName){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try{
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
                if (url!=null){
                    String protocal = url.getProtocol();
                    if (protocal.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20","");
                        addClass(classSet,packagePath,packageName);
                    }else if (protocal.equals("jar")){
                        JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                        if (jarURLConnection!=null){
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile!=null){
                                Enumeration<JarEntry> jarEntities = jarFile.entries();
                                while(jarEntities.hasMoreElements()){
                                    JarEntry jarEntry = jarEntities.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")){
                                        String className = jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                                        doAddClass(classSet,className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            lOGGER.error("get class set failure",ex );
            throw new RuntimeException(ex);
        }
        return classSet;
    }
    private static void addClass(Set<Class<?>> classSet, String packagePath, final String packageName){
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for(File file : files){
            String fileName = file.getName();
            if (file.isFile()){
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if (StringUtils.isNotEmpty(packageName)){
                    className = packageName +"." + className;
                }
                doAddClass(classSet, className);
            }else{
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packagePath)){
                    subPackagePath = packagePath +"/"+ subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)){
                    subPackageName = packageName +"." + subPackageName;
                }
                addClass(classSet, subPackagePath,subPackageName);
            }

        }
    }
    private static void doAddClass(Set<Class<?>> classSet, String className){
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }
}


