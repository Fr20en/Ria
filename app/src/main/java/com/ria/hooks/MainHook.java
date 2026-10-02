package com.ria.hooks;

import static android.util.Log.INFO;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;

/**
 * Ria hook 模块入口（libxposed API 102）。
 * hook 模板接入点：在 onPackageLoaded 中按 param.getPackageName() 过滤目标应用后执行 hook。
 */
public class MainHook extends XposedModule {

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);
        log(INFO, "Ria", "loaded in " + param.getPackageName());
    }
}
