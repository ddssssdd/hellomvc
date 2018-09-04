package com.sfu.framework.helper;

import com.sfu.framework.annotation.Inject;
import com.sfu.framework.util.ReflectionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Map;

public final class IocHelper {
    static{
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (beanMap!=null && !beanMap.isEmpty()){
            for(Map.Entry<Class<?>,Object>  beanEntity: beanMap.entrySet()){
                Class<?> beanClass = beanEntity.getKey();
                Object beanInstance = beanEntity.getValue();
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtils.isNotEmpty(beanFields)){
                    for(Field beanField: beanFields){
                        if (beanField.isAnnotationPresent(Inject.class)){
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance!=null){
                                ReflectionUtil.setField(beanInstance, beanField,beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
