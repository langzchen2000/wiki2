package com.jiawa.wiki2.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.wiki2.domain.Content;
import com.jiawa.wiki2.domain.Doc;
import com.jiawa.wiki2.domain.DocExample;
import com.jiawa.wiki2.mapper.ContentMapper;
import com.jiawa.wiki2.mapper.DocMapper;
import com.jiawa.wiki2.req.DocQueryReq;
import com.jiawa.wiki2.req.DocSaveReq;
import com.jiawa.wiki2.resp.DocQueryResp;
import com.jiawa.wiki2.resp.PageResp;
import com.jiawa.wiki2.util.CopyUtil;
import com.jiawa.wiki2.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {
    @Resource//这里的Resource是Java的注解，不是Spring的注解
    private DocMapper docMapper;
    @Resource//这里的Resource是Java的注解，不是Spring的注解
    private ContentMapper contentMapper;
    @Resource
    private SnowFlake snowFlakeService;
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);
        return respList;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlakeService.nextId());
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if(count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        if(content == null) return "";
        else return content.getContent();
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
}
