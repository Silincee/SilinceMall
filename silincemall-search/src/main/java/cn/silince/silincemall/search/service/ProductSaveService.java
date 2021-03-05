package cn.silince.silincemall.search.service;

import cn.silince.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    /** 
    * @description: 商品上架功能 
    */ 
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
