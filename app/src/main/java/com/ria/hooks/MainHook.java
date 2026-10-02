package com.ria.hooks;

import static android.util.Log.INFO;

import java.io.File;
import java.io.FileOutputStream;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;

/**
 * Ria hook 模块入口（libxposed API 102）。
 * hook 模板接入点：在 onPackageLoaded 中按 param.getPackageName() 过滤目标应用后执行 hook。
 */
public class MainHook extends XposedModule {

    static final String SELF_PACKAGE = "com.ria.hooks";

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);
        log(INFO, "Ria", "loaded in " + param.getPackageName());

        if (SELF_PACKAGE.equals(param.getPackageName())) {
            // LSPosed 已把模块自身加载进本进程：写激活标记供 UI 首页读取（48 小时内有效）
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
}
