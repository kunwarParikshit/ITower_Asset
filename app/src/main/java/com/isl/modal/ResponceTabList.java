package com.isl.modal;

import java.util.ArrayList;

/**
 * Created by dhakan on 2/17/2020.
 */

public class ResponceTabList {
    public ArrayList<BeanAssignHis> Assign;
    public ArrayList<BeanAlarmHis>  Alarm;


    public ArrayList<BeanAssignHis> getAssign() {
        return Assign;
    }

    public void setAssign(ArrayList<BeanAssignHis> assign) {
        Assign = assign;
    }

    public ArrayList<BeanAlarmHis> getAlarm() {
        return Alarm;
    }

    public void setAlarm(ArrayList<BeanAlarmHis> alarm) {
        Alarm = alarm;
    }
}
