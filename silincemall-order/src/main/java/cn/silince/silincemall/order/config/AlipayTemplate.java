package cn.silince.silincemall.order.config;

import cn.silince.silincemall.order.vo.PayVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000117617121";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public  String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCeTpkPabiXxnbDgETVS1I59AdKjIj7K4jrgUezQvqpjNQyKKCXcLktVh1u4g7miMIvzKybWWQ5by89njUxb6UyrK2Er+LLMvZ8Dk+GYn3WhuntjuIhSd7tlFsNelNnUmrTAIkOhRk7zYO8h1SfZmor12CkNG6EhLZo0ibt4vOUV18f315EJcYc4xIyMGVK1nE2gEuxiCGjXWNslx/z5WTaC64nllRLKN3m3owPciI46di5xV7+oi4FcXHY79K9DGBaLp39arjZ6qoc3VmN+FRcB808vf5I4EDTNiyGvD5WNS+CnMvCay/GCxIH4B7kovYPEHooO1PT3qdIRXHnh0JHAgMBAAECggEBAJsZZIqeSPWiw18df9L93czb0nzunZJ//8DEw8vU9qNBT/DorxZQoYzSjznBD1o8aQzN2drGRRSkY0NaisEpok+4sLSMYoY1Ixs+JhoCy0lwmyZdVB3LMzfPnzngPHDBZdne72kjbCqUWtpOONGHVMQpWwzmNdVt9Q1TwEJXldAiEzar762i9FlXH+cTd8IXFFhyVmkldcIz9uS27tNt4dqvWZ4ggYBlAxMDlH06jLwUg0XLgpDJzJmn4XFLjo4UEwy6Xbw5xzcD67YA2qGMGcmmxaqdycvwC0MSnFoAbcd/am8A+7t3SvkxsDoaq3lwvGAgkkAZOw+1S5t9NyykGRECgYEA0qtRZ88dxR9m/Y63kor5o2IMe7/lGPxPC8/oWXxcMKQyIu6iF3xJPfGjV8IXUqCMcVC9k/jrsbSWmcsxIpy4eGBNW4jxvLCF1C/GblM+7OONlrwzeUcRPzYIe5Moh7a99gqzIclIKBr6P6LiEu5ysLVoMiMTcodkIsCJGr1cBPkCgYEAwF7qppBS1/FrDynkOqn0Z7uy0Krng7lYbIZofJQq6SYyMnZhhq/uucP7QlVG1hdos57DiuDrZjXQMLs9AhFyIoqoT8wKY27rFhnSOYMb4p8WzN6Ym59Pl/VjDiUKVLT7kIjO0xzwl8idVE6dJhqWvrfq8S9eNcNecKwDkbjZkT8CgYEAxgu8CaURzYCJ55BFTwRLTqccKTHrA6QeA3K+cXGRkUCqyWAhDYcPq6X/8r5KAqlfnh9TOUpm+LZWZdo5JNrEKJYP4x9IciRYi3MmTg0AON5q+uuOUFLZCd2X9+QN1BedWTIR2I9KwDksTKqpcTaspybC/28uFxrkri7mNTcSzLECgYAMiiNhmCbOTL5dzq1B5bI40NNnJrzQqVRKPriw5jel2weKoozY6r70/QFz4XssmXoJu1+jveWaVAeuJYZkjB6UgdVW3kYPFTvdxPEfLpyyuQwTbq8j9c6KaUR7t45k1ydO1JwmxswoGLuoszcrNLB+3h5CeoP5nTKaxZu19Aa6oQKBgQDPo4L1r5XVFT1a+NYYxHS6Z9p6elXV1E4teDiqTO94y2bUYr6+mkXE/RgXrjBvKdE0iehtDUNz9NFKkmqfyZv3VoeDVCM0GMtbXbg+CUOfiSHNnZjEfkrYMkNqEh6XzOhuf+/+SCztighXgZTchaNN/NmyhggEjEyPS7yq7N47nA==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu326mCZkoRwp/kABDhb6IfK5gEzkZMt4mwMOd5xO2LZNk2hN1i6aJ8k1tC4y3nk212JLQOPFO1xn4e1ZNhdbxXXr70AbIU6CZ50AKiCGopz8JxunBp/FKb+jQy6qZ9Wmx2M0FmSu5CgkmoGc1QjUumht805EFWZxzhD5Yv0Wx6QEy/rQWk2MrDTrsNYlfryc//c8bv3UC/897UhL45y9NZX9dGZ80Kr+9bGIl5gc/UhqdYwq+syYCDslY2BwE2m4NIqFSuGY0O2PvX3WVo65QJSw0p6JDyHnfg//jScupYPbqk1gosyrreoC7qVT5WH1xCllhiTDcichwHt8liyrBQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://dce4a1301eda.ngrok.io/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    public String return_url = "http://member.silincemall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
