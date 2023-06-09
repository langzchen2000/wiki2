package com.jiawa.wiki2.resp;

public class UserLoginResp {
    private Long id;

    private String loginName;

    private String name;

    private Long token;

    public Long getToken() {
        return token;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserLoginResp{");
        sb.append("id=").append(id);
        sb.append(", loginName='").append(loginName).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", token=").append(token);
        sb.append('}');
        return sb.toString();
    }

    public void setToken(Long token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}