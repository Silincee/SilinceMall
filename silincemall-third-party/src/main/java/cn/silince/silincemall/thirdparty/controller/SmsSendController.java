package cn.silince.silincemall.thirdparty.controller;

import cn.silince.common.utils.R;
import cn.silince.silincemall.thirdparty.component.SmsComponent;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-24 19:14
 **/
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    private SmsComponent smsComponent;

    /**
    * @description: 提供给别的服务进行调用
    */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code) {
        try {
            System.out.println("第三方短信服务。。。。被调用了");
            smsComponent.sendSmsCode(phone,code);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return R.ok();
    }
}
