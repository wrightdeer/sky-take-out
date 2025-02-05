package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺状态相关接口")
public class ShopController {
    @Autowired
    private ShopService shopService;
    /**
     * 设置店铺状态
     * @param status 店铺状态，1表示开启，0表示关闭
     * @return 操作结果，包含成功信息
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态：{}", status == 1 ? "开启" : "关闭");
        shopService.setStatus(status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return 包含店铺状态的结果，状态值为1表示开启，0表示关闭
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getStatus(){
        Integer status = shopService.getStatus();
        return Result.success(status);
    }
}
