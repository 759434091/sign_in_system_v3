package team.a9043.sign_in_system.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表
 * 用户表实体
 *
 * @author a9043
 */
@Entity
public class SisUser {
    /**
     * 登录名，学号，用户名
     */
    @Id
    @Column(length = 20)
    private String suId;

    /**
     * 姓名，昵称
     */
    @Column(length = 20)
    private String suName;

    /**
     * 密文密码
     */
    @Column(length = 72)
    private String suPassword;

    /**
     * 微信小程序唯一openid
     */
    @Column(length = 28, unique = true)
    private String suOpenid;

    /**
     * 权限列表
     */
    @Column
    private String suAuthoritiesStr;

    public String getSuId() {
        return suId;
    }

    public void setSuId(String suId) {
        this.suId = suId;
    }

    public String getSuName() {
        return suName;
    }

    public void setSuName(String suName) {
        this.suName = suName;
    }

    public String getSuPassword() {
        return suPassword;
    }

    public void setSuPassword(String suPassword) {
        this.suPassword = suPassword;
    }

    public String getSuOpenid() {
        return suOpenid;
    }

    public void setSuOpenid(String suOpenid) {
        this.suOpenid = suOpenid;
    }

    public String getSuAuthoritiesStr() {
        return suAuthoritiesStr;
    }

    public void setSuAuthoritiesStr(String suAuthoritiesStr) {
        this.suAuthoritiesStr = suAuthoritiesStr;
    }

    public List<GrantedAuthority> getSuAuthorities() {
        if (null == suAuthoritiesStr) {
            return null;
        }
        return Arrays
            .stream(suAuthoritiesStr.split(","))
            .map(String::trim)
            .map(String::toUpperCase)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public void setSuAuthorities(List<String> suAuthorityList) {
        this.suAuthoritiesStr = String.join(",",
            (String[]) suAuthorityList.toArray());
    }
}
