package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "用户端购物车接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartServer shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车数据传输对象，包含要添加的商品信息
     * @return 添加结果，成功返回Result.success()
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 减少购物车
     * @param shoppingCartDTO 购物车数据传输对象，包含要减少的商品信息
     * @return 减少结果，成功返回Result.success()
     */
    @PostMapping("/sub")
    @ApiOperation("减少购物车")
    public Result sub(ShoppingCartDTO shoppingCartDTO){
        log.info("减少购物车：{}", shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
    /**
     * 查看购物车
     * @return 购物车商品列表，成功返回Result.success(list)
     */
    @GetMapping("/list")
    @ApiOperation("查询购物车")
    public Result<List<ShoppingCart>> list() {
        log.info("查询购物车");
        List<ShoppingCart> list = shoppingCartService.showShoppingCart();
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return 清空结果，成功返回Result.success()
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result clean() {
        log.info("清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }

}
