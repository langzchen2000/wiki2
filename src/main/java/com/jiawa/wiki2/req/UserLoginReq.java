package com.jiawa.wiki2.req;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserLoginReq {


@NotNull(message = "[username] should not be null")
    private String loginName;

    @NotNull(message = "Password shouldn't be empty")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "password or login name not correct")
    private String password;


    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserSaveReq{");
        sb.append(", loginName='").append(loginName).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
