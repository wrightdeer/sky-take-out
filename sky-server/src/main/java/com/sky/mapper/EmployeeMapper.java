package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username 员工用户名
     * @return 查询到的员工对象，若未找到则返回null
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee 待新增的员工对象
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Employee employee);

    /**
     * 分页查询员工信息
     * @param employeePageQueryDTO 分页查询条件对象
     * @return 分页查询结果
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 更新员工信息
     * @param employee 待更新的员工对象
     */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据ID查询员工信息
     * @param id 员工ID
     * @return 查询到的员工对象，若未找到则返回null
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

    /**
     * 根据员工ID查询密码
     * @param empId 员工ID
     * @return 查询到的密码字符串
     */
    @Select("select password from employee where id = #{empId}")
    String getPassword(Long empId);

}