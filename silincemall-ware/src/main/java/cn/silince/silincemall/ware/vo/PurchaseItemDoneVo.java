package cn.silince.silincemall.ware.vo;

import lombok.Data;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-16 11:18
 **/
@Data
public class PurchaseItemDoneVo {
    //  {"itemId":1,"status":3, "reason":""}
    private Long itemId;
    private  Integer status;
    private String reason;
}
