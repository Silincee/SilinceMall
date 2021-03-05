package cn.silince.silincemall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-23 22:26
 **/
@ToString
@Data
public  class SpuItemAttrGroupVo{
    private String groupName;
    private List<Attr> attrs;

}
