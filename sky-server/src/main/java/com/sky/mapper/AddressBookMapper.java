package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    /**
     * 查询地址簿
     * @param id 用户ID
     * @return 地址簿列表
     */
    @Select("select * from address_book where user_id = #{id}")
    List<AddressBook> list(Long id);

    /**
     * 新增地址
     * @param addressBook 地址信息对象
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label) " +
            "values (#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label})")
    void insert(AddressBook addressBook);

    /**
     * 清理默认地址
     * @param userId 用户ID
     */
    @Update("update address_book set is_default = 0 where user_id = #{userId}")
    void cleanDefault(Long userId);
    
    /**
     * 查询默认地址
     * @param userId 用户ID
     * @return 默认地址信息对象
     */
    @Select("select * from address_book where user_id = #{userId} and is_default = 1")
    AddressBook getDefault(Long userId);

    /**
     * 根据id查询地址
     * @param id 地址ID
     * @return 地址信息对象
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 修改地址
     * @param addressBook 地址信息对象
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id 地址ID
     */
    @Delete("delete from address_book where id = #{id}")
    void delete(Long id);
}