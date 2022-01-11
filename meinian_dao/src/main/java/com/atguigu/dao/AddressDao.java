package com.atguigu.dao;

import com.atguigu.pojo.Address;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressDao {
    List<Address> findAllMaps();


    Page findPage(@Param("queryString")String queryString);

    void addAddress(@Param("addressName")String addressName,@Param("lng") String lng, @Param("lat")String lat);

    void deleteById(Integer id);
}
