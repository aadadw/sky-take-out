package com.sky.mapper;

import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

//    新增员工
//    @Insert("insert into employee (id_number,name,phone,sex,username,create_time,update_time)" +
//            "values(#{idNumber},#{name},#{phone},#{sex},#{username},#{createTime},#{updateTime})")
    @Insert("insert into employee (name,username,password,phone,sex,id_number,create_time,update_time,create_user,update_user,status)" +
            "values (#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insert(Employee employee);


}
