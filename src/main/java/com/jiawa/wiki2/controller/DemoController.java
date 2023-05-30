package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.domain.Demo;
import com.jiawa.wiki2.domain.Test;
import com.jiawa.wiki2.service.DemoService;
import com.jiawa.wiki2.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class DemoController {
    @Resource
    private DemoService demoService;
    @GetMapping("/demo/list")
    public List<Demo> list() {
        return demoService.list();
    }

    



}