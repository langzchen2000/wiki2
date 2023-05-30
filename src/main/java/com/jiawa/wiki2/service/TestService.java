package com.jiawa.wiki2.service;

import com.jiawa.wiki2.domain.Test;
import com.jiawa.wiki2.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestService {
    @Resource
    private TestMapper testMapper;

    public List<Test> list() {
        return testMapper.list();
    }

}
