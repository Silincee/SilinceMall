package cn.silince.silincemall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: SilinceMall
 * @description: 二级分类Vo
 * @author: Silince
 * @create: 2021-02-18 20:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    private String catalog1Id; // 1级父分类id

    private List<Catelog3Vo> catalog3List; // 三级子分类

    private String id;

    private String name;


    /**
    * @description: 三级分类vo
    */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id;
        private String id;
        private String name;

    }
}
