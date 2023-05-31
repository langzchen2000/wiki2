package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.domain.Demo;
import com.jiawa.wiki2.domain.Ebook;
import com.jiawa.wiki2.service.DemoService;
import com.jiawa.wiki2.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class EbookController {
    @Resource
    private EbookService ebookService;
    @GetMapping("/ebook/list")
    public List<Ebook> list() {
        return ebookService.list();
    }
}