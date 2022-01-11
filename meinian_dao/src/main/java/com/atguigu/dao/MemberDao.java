package com.atguigu.dao;

import com.atguigu.pojo.Member;

public interface MemberDao {
    Member getMemberByTelephone(String telephone);

    void add(Member member);

    Integer findMemberCountBeforeDate(String regTime);

    int getTodayNewMember(String today);

    int getTotalMember();

    int getThisWeekAndMonthNewMember(String weekMonday);
}
