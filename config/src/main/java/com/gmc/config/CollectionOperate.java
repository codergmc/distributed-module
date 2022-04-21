package com.gmc.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CollectionOperate<T> {
    public static final CollectionOperate<List<?>> LIST = new CollectionOperate(List.class, LinkedList::new, (BiConsumer<List, Object>) List::add);
    public static final CollectionOperate<AbstractList<?>> ABSTRACT_LIST = new CollectionOperate(AbstractList.class, LinkedList::new, (BiConsumer<List, Object>) List::add);
    public static final CollectionOperate<ArrayList<?>> ARRAY_LIST = new CollectionOperate(ArrayList.class, ArrayList::new, (BiConsumer<List, Object>) List::add);
    public static final CollectionOperate<LinkedList<?>> LINKED_LIST = new CollectionOperate(LinkedList.class, LinkedList::new, (BiConsumer<List, Object>) List::add);
    public static final Map<Class<?>, CollectionOperate<?>> COLLECTION_OPERATE_MAP = new HashMap<>();
    private Class<T> rowClass;
    private Supplier<T> newInstance;
    private BiConsumer<T, Object> addOperate;
    private CiConsumer<T,Object,Object> addDoubleOperate;
    static {
        Field[] declaredFields = CollectionOperate.class.getDeclaredFields();
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && CollectionOperate.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    CollectionOperate<?> operate = (CollectionOperate<?>) field.get(null);
                    COLLECTION_OPERATE_MAP.put(operate.getRowClass(), operate);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public CollectionOperate(Class<T> aClass, Supplier<T> newInstance, BiConsumer<T, Object> addOperate) {
        this.rowClass = aClass;
        this.newInstance = newInstance;
        this.addOperate = addOperate;
    }

    public CollectionOperate(Class<T> aClass, Supplier<T> newInstance, CiConsumer<T, Object, Object> addDoubleOperate) {
        this.rowClass = aClass;
        this.newInstance = newInstance;
        this.addDoubleOperate = addDoubleOperate;
    }

    public T newInstance(){
        return newInstance.get();
    }
    public void merge(T t,Object object){
        addOperate.accept(t,object);
    }
    public void merge(T t,Object input1,Object input2){
        addDoubleOperate.accept(t,input1,input2);
    }
    public static CollectionOperate<?> findOperate(Class<?> aClass) {
        return COLLECTION_OPERATE_MAP.get(aClass);

    }

    public Class<T> getRowClass() {
        return rowClass;
    }
}
