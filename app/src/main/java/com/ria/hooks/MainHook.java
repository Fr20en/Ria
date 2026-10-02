package com.ria.hooks;

import static android.util.Log.INFO;

import java.io.File;
import java.io.FileOutputStream;
import java.util.function.Consumer;

import com.ria.hooks.app.MkzHooks;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;

/**
 * Ria hook 模块入口（libxposed API 102）。
 * 已接入目标：漫客栈 (com.xmtj.mkz)。新增目标时在 route() 里加一个 case + 新建 app/XxxHooks。
 */
public class MainHook extends XposedModule {

    static final String SELF_PACKAGE = "com.ria.hooks";

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);
        String pkg = param.getPackageName();
        log(INFO, "Ria", "loaded in " + pkg);

        if (SELF_PACKAGE.equals(pkg)) {
            writeActiveFlag(param);
            return;
        }
        route(param, pkg);
    }

    /** 按包名分发给各应用的 hook 实现。 */
    private void route(PackageLoadedParam param, String pkg) {
        if ("com.xmtj.mkz".equals(pkg)) {
            withClassLoader(param, cl -> MkzHooks.hook(this, cl));
        }
    }

    private void withClassLoader(PackageLoadedParam param, Consumer<ClassLoader> action) {
        ClassLoader cl;
        try {
            cl = param.getDefaultClassLoader();
        } catch (Throwable t) {
            log(INFO, "Ria", "classloader unavailable: " + t);
            return;
        }
        if (cl == null) {
            log(INFO, "Ria", "skip: no classloader for " + param.getPackageName());
            return;
        }
        action.accept(cl);
    }

    /** LSPosed 把模块加载进自身进程时写激活标记，供 UI 首页检测（48 小时内有效）。 */
    private void writeActiveFlag(PackageLoadedParam param) {
        try {
            File flag = new File(param.getApplicationInfo().dataDir, "cache/ria_active");
            File parent = flag.getParentFile();
            if (parent != null && parent.mkdirs()) {
                log(INFO, "Ria", "cache dir created");
            }
            try (FileOutputStream out = new FileOutputStream(flag)) {
                out.write(String.valueOf(System.currentTimeMillis()).getBytes());
            }
            flag.setReadable(true, false);
            flag.setWritable(true, false);
        } catch (Throwable t) {
            log(INFO, "Ria", "write active flag failed: " + t);
        }
    }
}
