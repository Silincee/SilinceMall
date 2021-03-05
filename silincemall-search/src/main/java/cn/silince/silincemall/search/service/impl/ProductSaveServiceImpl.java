package cn.silince.silincemall.search.service.impl;

import cn.silince.common.to.es.SkuEsModel;
import cn.silince.silincemall.search.config.SilincemallElasticSearchConfig;
import cn.silince.silincemall.search.constant.EsConstant;
import cn.silince.silincemall.search.service.ProductSaveService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-18 17:03
 **/
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 1 给es中建立索引 product,建立好映射关系


        // 2 给es中保存这些数据
        // BulkResponse bulk(BulkRequest bulkRequest, RequestOptions options)
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model : skuEsModels) {
            // 构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(model), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, SilincemallElasticSearchConfig.COMMON_OPTIONS);// 批量保存操作

        // TODO 如果存在批量错误
        boolean b = bulk.hasFailures();

        List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
        log.info("商品上架完成: {}", collect);

        return b;
    }
}
