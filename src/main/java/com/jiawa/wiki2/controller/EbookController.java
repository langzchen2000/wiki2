package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.req.EbookQueryReq;
import com.jiawa.wiki2.req.EbookSaveReq;
import com.jiawa.wiki2.resp.CommonResp;
import com.jiawa.wiki2.resp.EbookQueryResp;
import com.jiawa.wiki2.resp.PageResp;
import com.jiawa.wiki2.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class EbookController {
    @Resource
    private EbookService ebookService;
    @GetMapping("/ebook/list")
    public CommonResp list(EbookQueryReq req) {//EbookReq is POJO, 是封装请求的类
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/ebook/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}