package cn.silince.silincemall.product.exception;

import cn.silince.common.exception.BizCodeEnume;
import cn.silince.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * @program: SilinceMall
 * @description: 全局统一异常处理类,集中处理所有异常
 * @author: Silince
 * @create: 2021-02-07 20:48
 **/
@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "cn.silince.silincemall.product.controller")
@RestControllerAdvice(basePackages = "cn.silince.silincemall.product.controller")//需要捕获异常的controller
public class SilinceMallExceptionControllerAdvice {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){

      log.error("数据校验出现问题{},异常类型:{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();

        HashMap<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",errorMap);

    }

    @ExceptionHandler(value = Throwable.class)
    public  R handleException(Throwable throwable){
        log.error("错误",throwable);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
