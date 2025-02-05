package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 登录
     *
     * @param employeeLoginDTO 用户名和密码
     * @return 包含id、用户名、姓名、jwt令牌的EmployeeLoginVO对象
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        // TODO 将生成令牌过程封装进业务层
        EmployeeLoginVO employeeLoginVO = employeeService.login(employeeLoginDTO);

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return 成功信息
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出")
    public Result<String> logout() {
        log.info("退出登录");
        // TODO 删除Redis中的 jwt 令牌
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO 员工信息，包括id（可省略）、用户名、姓名、手机号、身份证、性别
     * @return 成功信息
     */
    @PostMapping
    @ApiOperation(value = "新增员工")
    public Result add(@RequestBody EmployeeDTO employeeDTO){

        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询员工信息
     * @param employeePageQueryDTO 分页查询条件，包括页码、每页大小、姓名、状态等
     * @return 包含员工信息的PageResult对象
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询员工信息")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询员工信息：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     * @param status 员工状态，1表示启用，0表示禁用
     * @param id 员工ID
     * @return 成功信息
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用禁用员工账号")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用禁用员工账号：{}, {}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }
    /**
     * 根据id查询员工信息
     * @param id 员工ID
     * @return 包含员工信息的Employee对象
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 修改员工信息
     * @param employeeDTO 员工信息，包括id、用户名、姓名、手机号、身份证、性别
     * @return 成功信息
     */
    @PutMapping
    @ApiOperation(value = "修改员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 修改密码
     * @param passwordEditDTO 密码修改信息，包括员工ID、旧密码、新密码
     * @return 成功信息
     */
    @PutMapping("/editPassword")
    @ApiOperation(value = "修改密码")
    public Result editPassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改密码：{}", passwordEditDTO);
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }
}
