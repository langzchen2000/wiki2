package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.req.EbookQueryReq;
import com.jiawa.wiki2.req.EbookSaveReq;
import com.jiawa.wiki2.resp.CommonResp;
import com.jiawa.wiki2.resp.EbookQueryResp;
import com.jiawa.wiki2.resp.PageResp;
import com.jiawa.wiki2.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class EbookController {
    @Resource
    private EbookService ebookService;
    @GetMapping("/ebook/list")
    public CommonResp list(@Valid EbookQueryReq req) {//EbookReq is POJO, 是封装请求的类
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/ebook/save")
    public CommonResp save(@RequestBody @Valid EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/ebook/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }

    @GetMapping("/ebook/search")
    public CommonResp search(@Valid EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}