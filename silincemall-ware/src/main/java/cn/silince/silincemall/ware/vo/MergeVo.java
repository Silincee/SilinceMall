package cn.silince.silincemall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: SilinceMall
 * @description: 采购项vo
 * @author: Silince
 * @create: 2021-02-15 23:09
 **/
@Data
public class MergeVo {
    private Long purchaseId; // 整单id
    private List<Long> items; // 合并项集合
}
