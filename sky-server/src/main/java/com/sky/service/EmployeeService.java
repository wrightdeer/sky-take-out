package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.vo.EmployeeLoginVO;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 包含员工登录信息的数据传输对象
     * @return 包含登录员工信息的视图对象
     */
    EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO 包含新员工信息的数据传输对象
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param employeePageQueryDTO 包含分页查询条件的数据传输对象
     * @return 分页查询结果对象，包含员工信息列表和总记录数
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据id修改员工状态
     * @param status 员工状态，0表示停用，1表示启用
     * @param id 员工ID
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工
     * @param id 员工ID
     * @return 包含员工信息的实体对象
     */
    Employee getById(Long id);

    /**
     * 修改员工信息
     * @param employeeDTO 包含更新员工信息的数据传输对象
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param passwordEditDTO 包含旧密码和新密码的数据传输对象
     */
    void editPassword(PasswordEditDTO passwordEditDTO);

}