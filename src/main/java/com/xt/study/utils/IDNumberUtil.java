package com.xt.study.utils;

import java.util.Calendar;

public class IDNumberUtil {

    public static String dateOfBirth(String idNo) {
        return idNo.substring(6, 10) + "-" + idNo.substring(10, 12) + "-" + idNo.substring(12, 14);
    }

    public static Integer age(String idNo) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return year - Integer.valueOf(idNo.substring(6, 10));
    }
}
