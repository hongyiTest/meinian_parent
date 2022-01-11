package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.MemberDao;
import com.atguigu.pojo.Member;
import com.atguigu.service.MemberService;
import com.atguigu.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService{

    @Autowired
    MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.getMemberByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> monthList) {
        List<Integer> memeberCountList = new ArrayList<Integer>();
        if(monthList!=null && monthList.size()>0){
            for (String months : monthList) {
                //String regTime = months+"-31";
                // 获取指定月份的最后一天
                String regTime =  DateUtils.getLastDayOfMonth(months);
                //  迭代过去12个月，每个月注册会员的数量，根据注册日期查询
                Integer memeberCount = memberDao.findMemberCountBeforeDate(regTime);
                memeberCountList.add(memeberCount);
            }
        }
        return memeberCountList;

    }
}
