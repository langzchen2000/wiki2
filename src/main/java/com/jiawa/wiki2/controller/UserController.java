package com.jiawa.wiki2.controller;


import com.alibaba.fastjson.JSONObject;
import com.jiawa.wiki2.req.UserLoginReq;
import com.jiawa.wiki2.req.UserQueryReq;
import com.jiawa.wiki2.req.UserSavePassReq;
import com.jiawa.wiki2.req.UserSaveReq;
import com.jiawa.wiki2.resp.CommonResp;
import com.jiawa.wiki2.resp.UserLoginResp;
import com.jiawa.wiki2.resp.UserQueryResp;
import com.jiawa.wiki2.resp.PageResp;
import com.jiawa.wiki2.service.UserService;
import com.jiawa.wiki2.util.SnowFlake;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;


@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SnowFlake snowFlake;

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

    @PostMapping("/user/login")
    public CommonResp login(@RequestBody @Valid UserLoginReq req) {
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp r = userService.login(req);

        //generate token
        Long token = snowFlake.nextId();
        r.setToken(token);
        redisTemplate.opsForValue().set(Long.toString(token), JSONObject.toJSONString(resp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(r);
        return resp;
    }

    @GetMapping("/user/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);
        return resp;
    }
}