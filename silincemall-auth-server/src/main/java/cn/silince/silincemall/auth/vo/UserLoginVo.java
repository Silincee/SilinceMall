package cn.silince.silincemall.auth.vo;

import lombok.Data;

/**
 * @program: SilinceMall
 * @description: 封装登陆传过来的参数
 * @author: Silince
 * @create: 2021-02-25 00:02
 **/
@Data
public class UserLoginVo {
    private String loginacct;
    private String password;
}
