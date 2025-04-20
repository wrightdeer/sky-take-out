package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.*;
import com.sky.interceptor.utils.JwtRedisUtil;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private JwtRedisUtil jwtRedisUtil;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 包含用户名和密码的登录信息
     * @return 包含员工登录信息和JWT令牌的EmployeeLoginVO对象
     */
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传来的密码进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        String userId = "admin:" + employee.getId();
        jwtRedisUtil.saveJwtToRedis(userId, token, jwtProperties.getAdminTtl() / 1000);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        //3、返回实体对象
        return employeeLoginVO;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 包含员工信息的EmployeeDTO对象
     */
    public void save(EmployeeDTO employeeDTO) {

        Employee employee = new Employee();

        //1、对象属性拷贝 将第一个参数对象的属性值拷贝到第二个参数对象中
        BeanUtils.copyProperties(employeeDTO, employee);

        //2、设置账号的状态，默认正常状态 1表示正常 0表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        //3、设置密码，默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        employeeMapper.insert(employee);

    }

    /**
     * 分页查询员工信息
     *
     * @param employeePageQueryDTO 包含分页查询条件的EmployeePageQueryDTO对象
     * @return 包含分页结果的PageResult对象
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());

        return pageResult;
    }

    /**
     * 启用禁用员工账号
     *
     * @param status 员工账号的状态，1表示启用，0表示禁用
     * @param id     员工ID
     */
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
        if (Objects.equals(status, StatusConstant.DISABLE)) {
            String userId = "admin:" + id;
            jwtRedisUtil.removeJwtFromRedis(userId);
        }
    }

    /**
     * 根据id查询员工
     *
     * @param id 员工ID
     * @return 包含员工信息的Employee对象
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }

    /**
     * 根据id修改员工信息
     *
     * @param employeeDTO 包含员工信息的EmployeeDTO对象
     */
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }

    /**
     * 修改密码
     *
     * @param passwordEditDTO 包含旧密码和新密码的PasswordEditDTO对象
     */
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        log.info("修改密码：{}", passwordEditDTO);
        // 获取旧密码
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        String password = employeeMapper.getPassword(BaseContext.getCurrentId());
        if (password == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 判断旧密码是否正确
        if (!password.equals(oldPassword)) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        Employee employee = Employee.builder()
                .id(BaseContext.getCurrentId().longValue())
                .password(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()))
                .build();
        employeeMapper.update(employee);
        // 先清除旧token
        String userId = "admin:" + BaseContext.getCurrentId();
        jwtRedisUtil.removeJwtFromRedis(userId);
        // 将本次的token存入redis
        String jwt = BaseContext.getCurrentJwt();
        jwtRedisUtil.saveJwtToRedis(userId, jwt, jwtProperties.getAdminTtl() / 1000);
    }
}
