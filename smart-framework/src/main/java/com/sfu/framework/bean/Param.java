package com.sfu.framework.bean;

import java.util.Map;

public class Param {
    private Map<String,Object> paramMap;
    public Param(Map<String,Object> paramMap){
        this.paramMap = paramMap;
    }
    public long getLong(String name){
        return Long.parseLong(paramMap.get(name).toString());
    }

    public Map<String,Object> getMap(){
        return paramMap;
    }

    public Object[] params(){
        return this.paramMap.values().toArray();
    }

}
