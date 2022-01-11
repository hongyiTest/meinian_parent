package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.AddressDao;
import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.pojo.Address;
import com.atguigu.service.AddressService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = AddressService.class)
@Transactional
public class AddressServiceImpl implements AddressService {
    @Autowired
    AddressDao addressDao;
    @Override
    public List<Address> findAllMaps() {
        List<Address> list = addressDao.findAllMaps();
        return list;
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //开启分页功能
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());//传入当前页和每页多少条数据
        //传入分页条件
        Page page = addressDao.findPage(queryPageBean.getQueryString());
        PageResult pageResult = new PageResult(page.getTotal(),page.getResult());//将返回的总记录数和结果集封装返回
        return pageResult;
    }

    @Override
    public void addAddress(String addressName, String lng, String lat) {
        addressDao.addAddress(addressName,lng,lat);
    }

    @Override
    public void deleteById(Integer id) {
        addressDao.deleteById(id);
    }
}
