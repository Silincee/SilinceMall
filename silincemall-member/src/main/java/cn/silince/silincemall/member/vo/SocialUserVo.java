package cn.silince.silincemall.member.vo;

import lombok.Data;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-25 12:03
 **/
@Data
public class SocialUserVo {

    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
