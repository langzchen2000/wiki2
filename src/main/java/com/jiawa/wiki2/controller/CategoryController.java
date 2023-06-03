package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.req.CategoryQueryReq;
import com.jiawa.wiki2.req.CategorySaveReq;
import com.jiawa.wiki2.resp.CommonResp;
import com.jiawa.wiki2.resp.CategoryQueryResp;
import com.jiawa.wiki2.resp.PageResp;
import com.jiawa.wiki2.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class CategoryController {
    @Resource
    private CategoryService categoryService;
    @GetMapping("/category/list")
    public CommonResp list(@Valid CategoryQueryReq req) {//CategoryReq is POJO, 是封装请求的类
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/category/save")
    public CommonResp save(@RequestBody @Valid CategorySaveReq req) {
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/category/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }

    @GetMapping("/category/search")
    public CommonResp search(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }
}