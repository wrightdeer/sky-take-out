package com.sky.controller.user;

import com.sky.constant.MessageConstant;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "用户端地址簿接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询地址簿
     * @return 地址簿列表
     */
    @GetMapping("/list")
    @ApiOperation("查询地址簿")
    public Result<List<AddressBook>> list() {
        log.info("查询地址簿");
        List<AddressBook> list = addressBookService.list();
        return Result.success(list);
    }

    /**
     * 新增地址簿
     * @param addressBook 地址簿对象
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation("新增地址簿")
    public Result add(@RequestBody AddressBook addressBook) {
        log.info("新增地址簿:{}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param addressBook 地址簿对象，包含要设置为默认的地址ID
     * @return 操作结果
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook){
        log.info("设置默认地址：{}", addressBook.getId());
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 查询默认地址
     * @return 默认地址对象，如果不存在则返回错误信息
     */
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault(){
        log.info("查询默认地址");
        AddressBook addressBook = addressBookService.getDefault();
        return addressBook == null ? Result.error(MessageConstant.DEFAULT_ADDRESS_NOT_FOUND) : Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id 地址ID
     * @return 地址对象
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id){
        log.info("根据id查询地址：{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook 地址簿对象，包含要修改的地址信息
     * @return 操作结果
     */
    @PutMapping
    @ApiOperation("修改地址")
    public Result update(@RequestBody AddressBook addressBook){
        log.info("修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 删除地址
     * @param id 地址ID
     * @return 操作结果
     */
    @DeleteMapping
    @ApiOperation("删除地址")
    public Result delete(Long id){
        log.info("删除地址：{}", id);
        addressBookService.delete(id);
        return Result.success();
    }
}
