package team.a9043.sign_in_system.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表
 * 用户表实体
 * <p>
 * abbr: su
 *
 * @author a9043
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
public class SisUser {
    /**
     * 登录名，学号，用户名
     */
    @Id
    @Column(length = 20)
    @NotNull
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
    @NotNull
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

    @OneToMany
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    private Collection<SisMonitorTrans> sisMonitorTrans = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    private Collection<SisCourse> sisCourses = new ArrayList<>();


    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    private Collection<SisJoinCourse> sisJoinCourses = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "suId", referencedColumnName = "suId")
    private Collection<SisSignInDetail> sisSignInDetails = new ArrayList<>();

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
