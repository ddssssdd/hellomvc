package com.sfu.chapter3;


import com.sfu.framework.annotation.Action;
import com.sfu.framework.annotation.Controller;
import com.sfu.framework.bean.Data;
import com.sfu.framework.bean.View;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Controller
public class HomeController {

    @Action(value = "get:/index")
    public Data index(String id){
        /*
        Hello hello =new HelloImpl();
        DynamicProxy dynamicProxy = new DynamicProxy(hello);
        Hello helloProxy = (Hello)Proxy.newProxyInstance(
                hello.getClass().getClassLoader(),
                hello.getClass().getInterfaces(),
                dynamicProxy);

        helloProxy.say();*/
        DynamicProxy dynamicProxy = new DynamicProxy(new HelloImpl());
        Hello helloProxy = dynamicProxy.getProxy();
        helloProxy.say();

        return new Data("this is a test");
    }

    @Action(value="get:/index2")
    public View index2(){
        //new HelloImpl().say();

        CGLibProxy proxy = new CGLibProxy();
        Hello helloProxy = proxy.getProxy(HelloImpl.class);
        helloProxy.say();

        return new View("index.jsp");
    }
    public interface Hello{
        void say();
    }
    public class HelloImpl implements Hello{
        public void say(){
            System.out.println("hello, this is Hello, world");
        }
    }
    public class DynamicProxy implements InvocationHandler{

        private Object target;
        public DynamicProxy(Object target){
            this.target = target;
        }

        public <T> T getProxy(){
            return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    this);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            before();
            Object result = method.invoke(target,args);
            after();
            return result;
        }
        public void before(){
            System.out.println("before");
        }
        public void after(){
            System.out.println("after");
        }

    }

    public class CGLibProxy implements MethodInterceptor {

        public CGLibProxy(){

        }
        public <T> T getProxy(Class<T> cls){
            return (T)Enhancer.create(cls,this);

        }
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            before();
            Object result = methodProxy.invoke(obj,args);//methodProxy.invokeSuper(obj, args);
            after();
            return result;
        }
        public void before(){
            System.out.println("before");
        }
        public void after(){
            System.out.println("after");
        }
    }
}
