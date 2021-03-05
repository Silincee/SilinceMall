package cn.silince.silincemall.search;

import cn.silince.silincemall.search.config.SilincemallElasticSearchConfig;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SilincemallSearchApplication.class})
public class SilincemallSearchApplicationTests {

    @Resource
    private RestHighLevelClient client;

    @Data
    class User{
        private String username;
        private String gender;
        private Integer age;
    }

    @Data
    @ToString
    static class Account{
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    /**
    * @description: 测试存储数据到es/更新也可以
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html
    */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setUsername("silince");
        user.setGender("man");
        user.setAge(18);
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON); // 要保存的内容
        // 执行操作
        IndexResponse index = client.index(indexRequest, SilincemallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    /**
    * @description: 复杂检索
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html
    */
    @Test
    public void searchData() throws Exception{
        // 1. 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 1.1 构造检索条件
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        // 1.2 按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);
        // 1.3 计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balanceAvg);

        searchRequest.source(sourceBuilder);



        // 2. 执行检索
        SearchResponse searchResponse = client.search(searchRequest, SilincemallElasticSearchConfig.COMMON_OPTIONS);
        // 3. 分析结果
        System.out.println("检索条件: "+sourceBuilder.toString());
        System.out.println(searchResponse.toString());
        // 3.1 获取到所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String string = searchHit.getSourceAsString();
            Account account = JSON.parseObject(string, Account.class);
            System.out.println("account: "+ account.toString());
        }
        // 3.2 获取这次检索到的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations!=null){
            Terms ageAgg1 = aggregations.get("ageAgg");
            for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                System.out.println("年龄: "+keyAsString+"==>"+bucket.getDocCount());
            }

            Avg balanceAvg1 = aggregations.get("balanceAvg");
            System.out.println("平均薪资: "+balanceAvg1.getValue());
        }
    }

}
