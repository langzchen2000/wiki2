package com.jiawa.wiki2.service;

import com.jiawa.wiki2.domain.Demo;
import com.jiawa.wiki2.domain.Ebook;
import com.jiawa.wiki2.mapper.DemoMapper;
import com.jiawa.wiki2.mapper.EbookMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<Ebook> list() {
        return ebookMapper.selectByExample(null);
    }

}
