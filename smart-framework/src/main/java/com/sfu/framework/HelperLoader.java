package com.sfu.framework;

import com.sfu.framework.helper.BeanHelper;
import com.sfu.framework.helper.ClassHelper;
import com.sfu.framework.helper.ControllerHelper;
import com.sfu.framework.helper.IocHelper;
import com.sfu.framework.util.ClassUtil;

public final class HelperLoader {
    public static void init(){
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for(Class<?> cls :classList){
            ClassUtil.loadClass(cls.getName(),true);
        }
    }
}
