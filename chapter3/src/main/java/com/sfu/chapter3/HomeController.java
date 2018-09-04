package com.sfu.chapter3;


import com.sfu.framework.annotation.Action;
import com.sfu.framework.annotation.Controller;
import com.sfu.framework.bean.Data;

@Controller
public class HomeController {

    @Action(value = "get:/index")
    public Data index(){
        return new Data("this is a test");
    }
}
