package com.bd.model;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * 系统用户
 * @author Administrator
 */
@Data
public class AdminUser {
    @Id
    private Integer id;

    private Integer tenantid;

    private String name;

    private String psw;

    private String email;

    private Integer creator;

    private Date createtime;

    private Integer flag;

    private Date logintime;

    private Integer updateuser;

    private Date updatetime;

    private String loginname;


}