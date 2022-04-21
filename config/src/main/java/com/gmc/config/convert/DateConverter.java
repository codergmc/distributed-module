package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    public static class NumberToDateConverter extends InExactSimpleTypeConverter<Number, Date> {


        public NumberToDateConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            long l = ((Number) value).longValue();
            return (T) new Date(l);
        }
    }

    public static class DateToLongConverter extends InExactSimpleTypeConverter<Date, Long> {


        public DateToLongConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            return (T) Long.valueOf(((Date) value).getTime());
        }
    }

    public static class CalendarToDateConverter extends ExactSimpleTypeConverter<Calendar, Date> {

        public CalendarToDateConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            return (T) ((Calendar) value).getTime();

        }
    }

    public static class DateToCalendarConverter extends ExactSimpleTypeConverter<Date, Calendar> {

        public DateToCalendarConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(((Date) value));
            return (T) calendar;

        }
    }

    public static class StringToDateConverter extends ExactSimpleTypeConverter<String, Date> {

        public StringToDateConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            String s = (String) value;
            String format;
            if (s.length() == 10) {
                format = "yyyy-MM-dd";
            } else if (s.length() == 19) {
                format = "yyyy-MM-dd HH:mm:ss";
            } else {
                throw new IllegalArgumentException();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            try {
                return (T) dateFormat.parse(format);
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }

        }


    }

    public static class DateToStringConverter extends ExactSimpleTypeConverter<Date, String> {


        public DateToStringConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            Date s = (Date) value;
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return (T) dateFormat.format(s);

        }


    }

}
