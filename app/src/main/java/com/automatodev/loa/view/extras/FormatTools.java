package com.automatodev.loa.view.extras;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatTools {
    private NumberFormat format;
    private Locale locale;
    public FormatTools(){
        locale = new Locale("pt","BR");
    }

    public String maskPhone(String phone){
        String newPhone = "("+phone.substring(0,2)+")"+
                phone.substring(2,7)+"-"+phone.substring(7,11);
        return newPhone;
    }

    public String decimalFormat(double value){
        format = NumberFormat.getCurrencyInstance(locale);
        return format.format(value);
    }

    public String dateFormatnoHour(long dateTime){
        SimpleDateFormat format = new SimpleDateFormat(" d MMM yyyy",locale);
        return format.format(dateTime*1000);
    }

    public String dateFormatWithHour(long dateTime){
        SimpleDateFormat format = new SimpleDateFormat(" d MMM yyyy - HH:mm",locale);
        return format.format(dateTime*1000);
    }



}
