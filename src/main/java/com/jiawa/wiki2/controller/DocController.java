package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.req.DocQueryReq;
import com.jiawa.wiki2.req.DocSaveReq;
import com.jiawa.wiki2.resp.DocQueryResp;
import com.jiawa.wiki2.resp.CommonResp;
import com.jiawa.wiki2.resp.PageResp;
import com.jiawa.wiki2.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
public class DocController {
    @Resource
    private DocService docService;
    @GetMapping("/doc/all")
    public CommonResp list() {//DocReq is POJO, 是封装请求的类
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;
    }
    @GetMapping("/doc/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/doc/save")
    public CommonResp save(@RequestBody @Valid DocSaveReq req) {
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/doc/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        CommonResp resp = new CommonResp<>();
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }

    @GetMapping("/doc/search")
    public CommonResp search(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }
}