package com.jcwang.store.cart.exception;

import com.jcwang.store.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 统一异常处理
 * @Created: with IntelliJ IDEA.
 * @author: jcwang
 **/

@Slf4j
@ControllerAdvice
public class RuntimeExceptionHandler {

    /**
     * 全局统一异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R handler(RuntimeException exception) {
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }


    @ExceptionHandler(CartExceptionHandler.class)
    public R userHandler(CartExceptionHandler exception) {
        log.error(exception.getMessage());
        return R.error("购物车无此商品");
    }
}
