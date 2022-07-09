package com.jcwang.store.order.web;

import com.jcwang.store.order.feign.CartFeignService;
import com.jcwang.store.order.feign.MemberFeignService;
import com.jcwang.store.order.interceptor.LoginUserInterceptor;
import com.jcwang.store.order.service.OrderService;
import com.jcwang.store.order.vo.*;
import com.jcwang.store.vo.MemberResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.model.IModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Controller
public class OrderWebController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();

        model.addAttribute("confirmOrderData", orderConfirmVo);

        // 展示确认页面信息
        return "confirm";
    }

    /**
     * 提交订单
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {

        // 去创建订单，验证令牌，验证价格，锁定库存
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
        // 最终下单成功，来到支付选择页面
        // 下单失败回到订单确认页面，重新确认订单信息

        if (responseVo.getCode() == 0) {
            // 成功，来到支付选择页面
            model.addAttribute("submitOrderResp", responseVo);
            return "pay";
        }

        String msg = "下单失败：";
        switch (responseVo.getCode()) {
            case 1: msg += "令牌校验失败，订单信息过期，请刷新在提交（redis中的token失效了）。"; break;
            case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
            case 3: msg += "库存锁定失败，商品库存不足"; break;
        }

        // 失败。回到订单返回页面
        redirectAttributes.addFlashAttribute("msg", msg);
        return "redirect:http://order.dinosaurstore.com/toTrade";
    }
}
