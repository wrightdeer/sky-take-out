package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 根据类型查询
     * @param type 分类类型
     * @return 分类列表
     */
    List<Category> listQuery(Integer type, Integer status);

    /**
     * 分页查询
     * @param categoryPageQueryDTO 查询条件
     * @return 分页数量与列表数据
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param category 分类信息
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    /**
     * 修改分类
     * @param category 分类信息
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /**
     * 删除分类
     * @param id 分类id
     */
    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    /**
     * 根据id查询分类
     * @param id 分类id
     * @return
     */
    @Select("select * from category where id = #{id}")
    Category getById(Long id);
}
