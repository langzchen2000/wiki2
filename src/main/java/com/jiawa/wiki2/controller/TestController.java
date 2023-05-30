package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.domain.Test;
import com.jiawa.wiki2.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {
    @Resource
    private TestService testService;
    @GetMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }



}