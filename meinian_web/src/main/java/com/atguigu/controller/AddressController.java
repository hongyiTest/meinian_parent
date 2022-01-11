package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Address;
import com.atguigu.service.AddressService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference
    AddressService addressService;

    @RequestMapping("/findAllMaps")
    public Map findAllMaps(){
        Map<String,Object> map = new HashMap();
        List<Map> gridnMaps = new ArrayList<>();
        List<Map> nameMaps = new ArrayList<>();
        List<Address> list =  addressService.findAllMaps();//从数据库中取出数据
        //从集合中取出数据
        for (Address address : list) {
            Map<String,String> nameMap = new HashMap<>();
            nameMap.put("addressName",address.getAddressName());
            nameMaps.add(nameMap);//向list集合中添加map集合，map中有key，value

            Map<String,String> gridnMap = new HashMap<>();
            gridnMap.put("lng",address.getLng());
            gridnMap.put("lat",address.getLat());
            gridnMaps.add(gridnMap);
        }

        map.put("gridnMaps",gridnMaps);
        map.put("nameMaps",nameMaps);
        return map;
    }
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = addressService.findPage(queryPageBean);//将分页数据传入服务层
        return pageResult;

    }
    @RequestMapping("/addAddress")
    public Result addAddress(@RequestBody Map<String,Object> map){
        String addressName = (String)map.get("addressName");
        String lng = String.valueOf(map.get("lng"));
        String lat = String.valueOf(map.get("lat"));
//        String addressName = address.getAddressName();
//        String lng = address.getLng();
//        String lat = address.getLat();
        addressService.addAddress(addressName,lng,lat);
        return new Result(true, "添加地点成功");

    }
    @RequestMapping("/deleteById")
    public Result deleteById(@Param("id") Integer id){
        try {
            addressService.deleteById(id);
            return new Result(true,"删除地点成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除地点失败");
        }
    }
}
