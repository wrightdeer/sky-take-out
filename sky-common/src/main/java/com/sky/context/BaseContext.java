package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static ThreadLocal<String> threadLocal1 = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
    public static void setCurrentJwt(String awt) {
        threadLocal1.set(awt);
    }
    public static String getCurrentJwt() {
        return threadLocal1.get();
    }
    public static void removeCurrentJwt() {
        threadLocal1.remove();
    }

}
