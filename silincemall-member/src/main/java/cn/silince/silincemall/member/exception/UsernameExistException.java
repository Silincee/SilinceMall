package cn.silince.silincemall.member.exception;

/**
 * @program: SilinceMall
 * @description: 用户已存在异常
 * @author: Silince
 * @create: 2021-02-24 21:28
 **/
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户名已存在");
    }
}
