package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.entity.Vacation;

import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
@CrossOrigin
@Controller
@RequestMapping("vacation")

public class VacationController extends BaseController {
    @Autowired
    private VacationService vacationService;

    @GetMapping("/index")
    public String index() {

        return "vacation/index.html";
    }

    @GetMapping("/test")
    public Date test() {
        Date date = new Date();
        Date endtime=getEndTime(date,6);
        //return "vacation/test.html";
        return endtime;
    }

    @ResponseBody
    @RequestMapping("/insert")
    public List<Vacation> insert(Vacation vacation)
    {
        GregorianCalendar gc =new GregorianCalendar();
        Date starttime=vacation.getStarttime();
        Date endtime=vacation.getEndtime();
        List<Vacation> addList=new ArrayList<Vacation>();
        if(starttime!=null && endtime!=null) {

            vacation.setCreatetime(new Date());
            vacation.setCreator("");

            vacation.setCreatorcode("");
            vacation.setOrgcode("");


            vacation.setOrgname("");
            while (starttime.compareTo(endtime) < 0) {
                vacation.setSpecifiedday(starttime);

                vacationService.delete(starttime);
                int result = vacationService.insert(vacation);
                if(result == 1) {
                    Vacation newVacation=new Vacation();
                    newVacation.setSpecifiedday(vacation.getSpecifiedday());
                    newVacation.setType(vacation.getType());
                    addList.add(newVacation);
                }
                gc.setTime(starttime);
                gc.add(5, 1);
                starttime = gc.getTime();
            }

        }
        return addList;
    }

    @ResponseBody
    @RequestMapping("/query")
    public List<Vacation> query(int month,int year)
    {
        return vacationService.getByMonth(month,year);
    }


    @ResponseBody
    @RequestMapping("/getEndTime")
    public Date getEndTime(Date starttime,int interval){
            return vacationService.getEndTime(starttime, interval);
    }

    @ResponseBody
    @RequestMapping("/getStrEndTime")
    public String getStrEndTime(String starttime,int interval) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dtstarttime = format.parse(starttime);
            return vacationService.getEndTime(dtstarttime, interval).toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
    }


}
