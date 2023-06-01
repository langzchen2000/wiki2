package com.jiawa.wiki2.service;

import com.jiawa.wiki2.domain.Demo;
import com.jiawa.wiki2.domain.Ebook;
import com.jiawa.wiki2.domain.EbookExample;
import com.jiawa.wiki2.mapper.DemoMapper;
import com.jiawa.wiki2.mapper.EbookMapper;
import com.jiawa.wiki2.req.EbookReq;
import com.jiawa.wiki2.resp.EbookResp;
import com.jiawa.wiki2.util.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> respList = CopyUtil.copyList(ebookList, EbookResp.class);


        return respList;
    }
}
