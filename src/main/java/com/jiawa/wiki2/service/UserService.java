package com.jiawa.wiki2.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.wiki2.domain.User;
import com.jiawa.wiki2.domain.UserExample;
import com.jiawa.wiki2.exception.BusinessException;
import com.jiawa.wiki2.exception.BusinessExceptionCode;
import com.jiawa.wiki2.mapper.UserMapper;
import com.jiawa.wiki2.req.UserLoginReq;
import com.jiawa.wiki2.req.UserQueryReq;
import com.jiawa.wiki2.req.UserSavePassReq;
import com.jiawa.wiki2.req.UserSaveReq;
import com.jiawa.wiki2.resp.UserLoginResp;
import com.jiawa.wiki2.resp.UserQueryResp;
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
public class UserService {
    @Resource//这里的Resource是Java的注解，不是Spring的注解
    private UserMapper userMapper;
    @Resource
    private SnowFlake snowFlakeService;
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if(req.getLoginName() != null) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(), req.getSize());//current page, page size
        List<User> userList = userMapper.selectByExample(userExample);

        List<UserQueryResp> respList = CopyUtil.copyList(userList, UserQueryResp.class);
        PageInfo<UserQueryResp> pageInfo = new PageInfo<>();
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            User u = selectByLoginName(req.getLoginName());
            if(u != null) {
                // 用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            } else {
                user.setId(snowFlakeService.nextId());
                userMapper.insert(user);
            }
        } else {
            // 更新
            user.setLoginName(null);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String LoginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList.size() == 0) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    public void resetPassword(UserSavePassReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public UserLoginResp login(UserLoginReq req) {
        User user = selectByLoginName(req.getLoginName());
        if(user == null) {
            // 用户名不存在
            LOG.info("用户名不存在, {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        } else {
            if(user.getPassword().equals(req.getPassword())) {
                // 登录成功
                UserLoginResp userLoginResp = CopyUtil.copy(user, UserLoginResp.class);
                return userLoginResp;
            } else {
                // 密码不对
                LOG.info("密码不对, 输入密码：{}, 数据库密码：{}", req.getPassword(), user.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }

}
