package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

import java.util.Comparator;

public interface TypeConverter {


    <T> T convert(Object value, TypeWrapper fromType, TypeWrapper toType);

    ConvertSupportResult support(Object value,TypeWrapper fromType, TypeWrapper toType);
    ConvertSupportResult support(TypeWrapper fromType, TypeWrapper toType);
    void configTypeConverter(TypeConverters typeConverters);
    class ConvertSupportResult implements Comparable<ConvertSupportResult> {
        // -1 不支持，值越小优先级越高
        private TypeConverter typeConverter;
        private int supportHierarchyDistance = -1;
        public static ConvertSupportResultComparator comparator = new ConvertSupportResultComparator();

        public ConvertSupportResult(TypeConverter typeConverter, int supportHierarchyDistance) {
            this.typeConverter = typeConverter;
            this.supportHierarchyDistance = supportHierarchyDistance;
        }

        public ConvertSupportResult(int distance) {
            this.supportHierarchyDistance = distance;

        }

        public ConvertSupportResult merge(ConvertSupportResult other) {
            if (this.supportHierarchyDistance < 0 || other.getSupportHierarchyDistance() < 0) {
                return ConvertSupportResult.ofUnSupport();
            }
            return ConvertSupportResult.of(this.supportHierarchyDistance += other.getSupportHierarchyDistance());
        }
        public ConvertSupportResult mergeChild(ConvertSupportResult other) {
            return this;
        }

        public int getSupportHierarchyDistance() {
            return supportHierarchyDistance;
        }

        public TypeConverter getTypeConverter() {
            return typeConverter;
        }

        public static ConvertSupportResult of(int distance) {
            return new ConvertSupportResult(distance);
        }

        public static ConvertSupportResult ofUnSupport() {
            return of(-1);
        }

        public static ConvertSupportResult of(TypeConverter typeConverter, int distance) {
            return new ConvertSupportResult(typeConverter, distance);
        }

        public ConvertSupportResult setTypeConverter(TypeConverter typeConverter) {
            this.typeConverter = typeConverter;
            return this;
        }

        public boolean isSupport() {
            return supportHierarchyDistance >= 0;
        }

        @Override
        public int compareTo(ConvertSupportResult other) {
            if (this.supportHierarchyDistance < 0) {
                return 1;
            }
            if (other.getSupportHierarchyDistance() < 0) {
                return -1;
            }
            return supportHierarchyDistance - other.getSupportHierarchyDistance();

        }

        public static class ConvertSupportResultComparator implements Comparator<ConvertSupportResult> {

            @Override
            public int compare(ConvertSupportResult o1, ConvertSupportResult o2) {
                return o1.compareTo(o2);
            }
        }

    }


}
