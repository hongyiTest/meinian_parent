package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.service.MemberService;

import com.atguigu.service.ReportService;
import com.atguigu.service.SetmealService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    MemberService memberService;
    @Reference
    SetmealService setmealService;
    @Reference
    ReportService reportService;
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Map<String,Object> map = reportService.getBusinessReportData();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        try {
            List<String> setmealNames = new ArrayList<>();
            List<Map> setmealCount = setmealService.getSetmealReport();
            //对上面这个集合做迭代
            for (Map map : setmealCount) {
                String setmealName = (String) map.get("name");
                setmealNames.add(setmealName);//将数据套餐名字循环扔进集合中
            }
            Map map = new HashMap();
            //传入map集合中的数据
            map.put("setmealNames",setmealNames);
            map.put("setmealCount",setmealCount);//重点是取这个数据，里面有套餐名字
            return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
    }
    @RequestMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            //1.拿数据
            Map result = reportService.getBusinessReportData();
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");


            //2.获取模板文件的绝对路径
            String filepath = request.getSession().getServletContext().getRealPath("template")+ File.separator+"report_template.xlsx";

            //3.工作簿
            Workbook workbook = new XSSFWorkbook(new File(filepath));

            //4.写数据，找到列，再找行，写入数据，下标都是从0开始
            //创建工作表，指定工作表名称，根据下标0
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日出游数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周出游数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月出游数

            int rowNum = 12;
            for(Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            //5.输出文件，以流形式文件下载，另存为操作
            ServletOutputStream out = response.getOutputStream();

            // 下载的数据类型（excel类型）
            response.setContentType("application/vnd.ms-excel");
            // 设置下载形式(通过附件的形式下载)
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");

            workbook.write(out); //写给浏览器，文件下载

            //6.关闭
            out.flush();
            out.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            //跳转错误页面
            request.getRequestDispatcher("/pages/error/downloadError.html").forward(request,response);
        } /*finally {

        }*/

    }
    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        try {
            // 获取日历对象
            Calendar calendar = Calendar.getInstance();
            //根据当前时间，获取前12个月的日历(当前日历2020-02，12个月前，日历时间2019-03)
            //第一个参数，日历字段
            //第二个参数，要添加到字段中的日期或时间
            calendar.add(Calendar.MONTH,-12);

            List<String> list = new ArrayList<String>();
            for(int i=0;i<12;i++){
                //第一个参数是月份 2018-7
                //第二个参数是月份+1个月
                calendar.add(Calendar.MONTH,1);
                list.add(new SimpleDateFormat("yyyy-MM").format(calendar.getTime()));
            }

            Map<String,Object> map = new HashMap<String,Object>();
            // 把过去12个月的日期存储到map里面
            map.put("months",list);
            // 查询所有的会员
            List<Integer> memberCount = memberService.findMemberCountByMonth(list);
            map.put("memberCount",memberCount);

            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);

        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, MessageConstant.QUERY_SETMEALLIST_FAIL);
        }
    }
}
