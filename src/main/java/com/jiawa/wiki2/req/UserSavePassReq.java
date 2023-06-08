package com.jiawa.wiki2.req;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserSavePassReq {

    private Long id;
    @NotNull(message = "Password shouldn't be empty")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "password should contain number and alphabets")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserSavePassReq{");
        sb.append("id=").append(id);
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
