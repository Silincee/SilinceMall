package cn.silince.silincemall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-26 00:14
 **/
@Data
@ToString
public class UserInfoTo {
    private Long userId; // 用户登陆会有用户id
    private String userKey; // 没登陆则存在一个临时cookie user-key标识用户身份，一个月后过期；

    private boolean tempUser=false; // false表示cookie中不存在临时用户
}
