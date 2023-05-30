package com.jiawa.wiki2.service;

import com.jiawa.wiki2.domain.Demo;
import com.jiawa.wiki2.domain.Test;
import com.jiawa.wiki2.mapper.DemoMapper;
import com.jiawa.wiki2.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DemoService {
    @Resource
    private DemoMapper demoMapper;

    public List<Demo> list() {
        return demoMapper.selectByExample(null);
    }

}
