package cn.silince.silincemall.member.exception;

/**
 * @program: SilinceMall
 * @description: 用户存在异常
 * @author: Silince
 * @create: 2021-02-24 21:28
 **/
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号已存在");
    }
}
