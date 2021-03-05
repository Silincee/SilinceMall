package cn.silince.silincemall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: SilinceMall
 * @description: 采购完成vo
 * @author: Silince
 * @create: 2021-02-16 11:17
 **/
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id; // 采购单id
    private List<PurchaseItemDoneVo>  items; // 采购需求列表
}
