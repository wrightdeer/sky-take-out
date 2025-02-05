package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 查询地址簿
     * @return 地址簿列表
     */
    List<AddressBook> list();

    /**
     * 新增地址
     * @param addressBook 地址对象
     */
    void add(AddressBook addressBook);

    /**
     * 设置默认地址
     *
     * @param addressBook 地址对象
     */
    void setDefault(AddressBook addressBook);

    /**
     * 获取默认地址
     * @return 默认地址对象
     */
    AddressBook getDefault();

    /**
     * 根据id查询
     * @param id 地址ID
     * @return 地址对象
     */
    AddressBook getById(Long id);

    /**
     * 修改地址
     * @param addressBook 地址对象
     */
    void update(AddressBook addressBook);

    /**
     * 删除地址
     * @param id 地址ID
     */
    void delete(Long id);
}