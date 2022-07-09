package com.jcwang.store.order.web;

import com.jcwang.store.order.entity.OrderEntity;
import com.jcwang.store.order.service.OrderService;
import com.jcwang.store.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class PayController {

    @Autowired
    OrderService orderService;

    @GetMapping(value = "/aliPayOrder")
    public String payOrder(@RequestParam("orderSn") String orderSn) {
        PayVo payVo = orderService.getOrderPay(orderSn);

        // TODO 支付宝的话，需要首先验证签名

        orderService.handlePayResult(payVo);

        return "redirect:http://member.dinosaurstore.com/memberOrder.html";
//        return "redirect:http://dinosaurstore.com";
    }

    /**
     * 根据订单号查询订单状态的API
     * @param orderId
     * @return
     */
    @GetMapping(value = "/queryByOrderId")
    @ResponseBody
    public OrderEntity queryByOrderId(@RequestParam("orderId") String orderId) {
        log.info("查询支付记录...");
        return orderService.getOrderByOrderSn(orderId);
    }

//    @Autowired
//    private AlipayTemplate alipayTemplate;
//
//    @Autowired
//    private BestPayService bestPayService;
//
//    @Resource
//    private WxPayConfig wxPayConfig;
//
//    /**
//     * 用户下单:支付宝支付
//     * 1、让支付页让浏览器展示
//     * 2、支付成功以后，跳转到用户的订单列表页
//     * @param orderSn
//     * @return
//     * @throws AlipayApiException
//     */
//    @ResponseBody
//    @GetMapping(value = "/aliPayOrder",produces = "text/html")
//    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
//
//        PayVo payVo = orderService.getOrderPay(orderSn);
//        String pay = alipayTemplate.pay(payVo);
//        System.out.println(pay);
//        return pay;
//    }


//    /**
//     * 微信支付
//     * @param orderSn
//     * @return
//     */
//    @GetMapping(value = "/weixinPayOrder")
//    public String weixinPayOrder(@RequestParam("orderSn") String orderSn, Model model) {
//
//        OrderEntity orderInfo = orderService.getOrderByOrderSn(orderSn);
//
//        if (orderInfo == null) {
//            throw new RuntimeException("订单不存在");
//        }
//
//        PayRequest request = new PayRequest();
//        request.setOrderName("4559066-最好的支付sdk");
//        request.setOrderId(orderInfo.getOrderSn());
//        request.setOrderAmount(0.01);
//        request.setPayTypeEnum(WXPAY_NATIVE);
//
//        PayResponse payResponse = bestPayService.pay(request);
//        payResponse.setOrderId(orderInfo.getOrderSn());
//        log.info("发起支付 response={}", payResponse);
//
//        //传入前台的二维码路径生成支付二维码
//        model.addAttribute("codeUrl",payResponse.getCodeUrl());
//        model.addAttribute("orderId",payResponse.getOrderId());
//        model.addAttribute("returnUrl",wxPayConfig.getReturnUrl());
//
//        return "createForWxNative";
//    }




}
