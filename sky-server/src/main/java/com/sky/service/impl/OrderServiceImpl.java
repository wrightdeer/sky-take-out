package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.BaiduMapUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private BaiduMapUtil baiduMapUtil;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO 订单提交数据传输对象
     * @return 订单提交结果视图对象
     * @throws IOException 可能抛出的IO异常
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) throws IOException {
        // 处理业务异常（地址簿为空、购物车为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            // 抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();

        // 处理业务异常（客户地址超出配送范围）
        String shopAddress = baiduMapUtil.getShopAddress();
        String address1 = baiduMapUtil.addressToLatLng(shopAddress);
        String address2 = baiduMapUtil.addressToLatLng(address);
        Integer distance = baiduMapUtil.calculateDistance(address1, address2);
        if (distance == null || distance > 5000) {
            // 抛出业务异常
            throw new OrderBusinessException(MessageConstant.CANNOT_DELIVER_TO_THE_ADDRESS);
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        String username = userMapper.getById(userId).getName();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            // 抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orders.setUserName(username);
        orders.setAddress(address);
        orderMapper.insert(orders);

        // 向订单明细表插入n条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        // 清空购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        // 封装VO并返回

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO 订单支付数据传输对象
     * @return 订单支付结果视图对象
     * @throws Exception 可能抛出的异常
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        /*//调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;*/
        // 由于没有企业账号，无法调用微信支付接口，所以暂时跳过支付，更新订单状态
        Long id = orderMapper.getIdByUserIdAndNumber(userId, ordersPaymentDTO.getOrderNumber());
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);

        // 支付成功，通知管理端，通过websocket进行 type orderId content
        Map map = new HashMap();
        map.put("type", 1); // 1 来单提醒 2 客户催单
        map.put("orderId", id);
        map.put("content", "订单号：" + ordersPaymentDTO.getOrderNumber());

        webSocketServer.sendToAllClient(JSON.toJSONString(map));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        return vo;
    }

    /**
     * 支付成功，修改订单状态，由于未实现微信支付，此方法不会被调用
     *
     * @param outTradeNo 外部交易号
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 分页查询订单
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @param status 订单状态
     * @return 分页结果对象
     */
    public PageResult pageQuery(int page, int pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<Orders> pageInfo = (Page<Orders>) orderMapper.list(ordersPageQueryDTO);
        List<OrderVO> orderVOList = new ArrayList<>();
        String username = userMapper.getById(BaseContext.getCurrentId()).getName();

        if (pageInfo != null) {
            List<Orders> ordersList = pageInfo.getResult();
            for (Orders orders : ordersList) {
                Long ordersId = orders.getId();
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(ordersId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVO.setUserName(username);
                orderVOList.add(orderVO);
            }
            return new PageResult(pageInfo.getTotal(), orderVOList);
        }
        return null;
    }

    /**
     * 订单详情
     *
     * @param id 订单ID
     * @return 订单详情视图对象
     */
    public OrderVO getOrderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders != null) {
            Long userId = orders.getUserId();
            String username = userMapper.getById(userId).getName();
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
            OrderVO orderVO = new OrderVO();
            orderVO.setUserName(username);
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            return orderVO;
        }
        return null;
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     */
    public void cancel(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders != null) {
            if (orders.getPayStatus() == Orders.PAID) {
                // 执行退款

                // 设置订单支付状态为退款
                orders.setPayStatus(Orders.REFUND);
            }
            LocalDateTime orderTime = orders.getOrderTime();
            if (LocalDateTime.now().isAfter(orderTime.plusMinutes(15))) {
                orders.setCancelReason("支付超时");
            } else {
                orders.setCancelReason("用户取消");
            }
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelTime(LocalDateTime.now());
            orderMapper.update(orders);
        }
    }

    /**
     * 分页查询订单
     *
     * @param ordersPageQueryDTO 订单分页查询数据传输对象
     * @return 分页结果对象
     */
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> pageInfo = (Page<Orders>) orderMapper.list(ordersPageQueryDTO);
        if (pageInfo != null) {
            List<Orders> ordersList = pageInfo.getResult();
            List<OrderVO> orderVOList = new ArrayList<>();
            for (Orders orders : ordersList) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
                StringBuilder detailsStringBuilder = new StringBuilder();
                for (OrderDetail orderDetail : orderDetails) {
                    detailsStringBuilder.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).append(",");
                }

                orderVO.setOrderDishes(detailsStringBuilder.toString());
                orderVOList.add(orderVO);
            }
            return new PageResult(pageInfo.getTotal(), orderVOList);
        }
        return null;
    }

    /**
     * 再来一单
     *
     * @param id 订单ID
     */
    @Transactional
    public void repetition(Long id) {
        // 获取用户id
        Long userId = BaseContext.getCurrentId();

        // 先获取订单菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 转换成购物车数据
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .userId(userId)
                    .name(orderDetail.getName())
                    .image(orderDetail.getImage())
                    .dishId(orderDetail.getDishId())
                    .setmealId(orderDetail.getSetmealId())
                    .dishFlavor(orderDetail.getDishFlavor())
                    .number(orderDetail.getNumber())
                    .amount(orderDetail.getAmount())
                    .createTime(LocalDateTime.now())
                    .build();
            shoppingCartList.add(shoppingCart);
        }

        // 添加到购物车
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 订单统计
     *
     * @return 订单统计视图对象
     */
    public OrderStatisticsVO statistics() {
        return orderMapper.statistics();
    }

    /**
     * 接单
     *
     * @param id 订单ID
     */
    public void confirm(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     *
     * @param id 订单ID
     */
    public void delivery(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO 订单拒单数据传输对象
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .cancelReason(ordersRejectionDTO.getRejectionReason())
                .build();
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     *
     * @param id 订单ID
     */
    public void complete(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO 订单取消数据传输对象
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

    /**
     * 催单
     *
     * @param id 订单ID
     */
    public void reminder(Long id) {
        // 先获取订单号
        String orderNumber = orderMapper.getById(id).getNumber();

        // 校验订单是否存在
        if (orderNumber == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 构建推送消息
        Map map = new HashMap();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "订单号：" + orderNumber);

        // 通过websocket向客户端发送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
