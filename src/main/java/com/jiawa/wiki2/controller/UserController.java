package com.jiawa.wiki2.controller;


import com.jiawa.wiki2.req.UserQueryReq;
import com.jiawa.wiki2.req.UserSavePassReq;
import com.jiawa.wiki2.req.UserSaveReq;
import com.jiawa.wiki2.resp.CommonResp;
import com.jiawa.wiki2.resp.UserQueryResp;
import com.jiawa.wiki2.resp.PageResp;
import com.jiawa.wiki2.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class UserController {
    @Resource
    private UserService userService;
    @GetMapping("/user/list")
    public CommonResp list(@Valid UserQueryReq req) {//UserReq is POJO, 是封装请求的类
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/user/save")
    public CommonResp save(@RequestBody @Valid UserSaveReq req) {
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/user/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @GetMapping("/user/search")
    public CommonResp search(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/user/reset-password")
    public CommonResp reset(@RequestBody @Valid UserSavePassReq req) {
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }
}