package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateConverterTest {
    @Test
    void testNumberToDate(){
        TypeConverters typeConverters = new TypeConverters(Collections.emptyList(),new ConvertSupportResultFactory());
        Date date = new Date();
        long time = date.getTime();
        Object convert = typeConverters.convert(time, TypeWrapper.of(Long.class), TypeWrapper.of(Date.class));
        assertTrue(date.equals(convert));

    }
    @Test
    void testDateToLong(){
        TypeConverters typeConverters = new TypeConverters(Collections.emptyList(),new ConvertSupportResultFactory());
        Date date = new Date();
        long time = date.getTime();
        long convert = typeConverters.convert(date, TypeWrapper.of(Date.class), TypeWrapper.of(Long.class));
        assertTrue(time==convert);

    }


}