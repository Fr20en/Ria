package com.ria.hooks;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

/**
 * libxposed API 102 无 XposedHelpers，这里补齐 hook 模板所需的最小反射工具集。
 * 所有方法失败时只记日志不抛异常，单个 hook miss 不影响其余 hook。
 */
public final class HookHelper {

    private final XposedModule mod;

    public HookHelper(XposedModule mod) {
        this.mod = mod;
    }

    private void log(String msg) {
        mod.log(Log.INFO, "Ria", msg);
    }

    /** findClass：找不到返回 null 并记 [MISS]。 */
    public Class<?> findClass(String name, ClassLoader cl) {
        try {
            return Class.forName(name, false, cl);
        } catch (Throwable t) {
            log("[MISS] class " + name);
            return null;
        }
    }

    /** 在类及其父类链上找声明方法（含私有），找不到返回 null 并记 [MISS]。 */
    public Method findMethod(Class<?> c, String name, Class<?>... params) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            try {
                Method m = k.getDeclaredMethod(name, params);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) {
            }
        }
        log("[MISS] method " + c.getName() + "." + name);
        return null;
    }

    /** hook 方法使其恒返回固定值（对应模板 objectHook.hookReturn）。 */
    public boolean hookReturn(Method m, Object fixed) {
        if (m == null) return false;
        try {
            mod.hook(m).intercept(chain -> fixed);
            log("[OK] " + m.getDeclaringClass().getName() + "." + m.getName() + " -> " + fixed);
            return true;
        } catch (Throwable t) {
            log("[FAIL] hook " + m.getName() + ": " + t);
            return false;
        }
    }

    /** hook 方法：先清空第一个 List 参数再执行原方法（广告数据源过滤）。 */
    public boolean hookClearListArg(Method m) {
        if (m == null) return false;
        try {
            mod.hook(m).intercept(chain -> {
                Object a = chain.getArg(0);
                if (a instanceof List) {
                    ((List<?>) a).clear();
                }
                return chain.proceed();
            });
            log("[OK] clear-list " + m.getDeclaringClass().getName() + "." + m.getName());
            return true;
        } catch (Throwable t) {
            log("[FAIL] hook clear-list " + m.getName() + ": " + t);
            return false;
        }
    }

    /** hook 指定类的全部 void 方法使其直接返回（广告 SDK 加载兜底）。 */
    public int hookAllVoidReturn(Class<?> c) {
        if (c == null) return 0;
        int n = 0;
        for (Method m : c.getDeclaredMethods()) {
            if (m.getReturnType() == void.class) {
                try {
                    mod.hook(m).intercept(chain -> null);
                    n++;
                } catch (Throwable ignored) {
                }
            }
        }
        log("[OK] void-block " + c.getName() + " x" + n);
        return n;
    }
}
