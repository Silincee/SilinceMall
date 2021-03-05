package cn.silince.silincemall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @program: SilinceMall
 * @description: 会员注册vo
 * @author: Silince
 * @create: 2021-02-24 21:15
 **/
@Data
public class MemberRegistVo {

    private String username;


    private String password;


    private String phone;
}
