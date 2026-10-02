package com.ria.hooks;

import android.util.Log;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleContext;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;

/**
 * Ria hook 模块入口（libxposed API 102）。
 * hook 模板接入点：在 onPackageLoaded 中按 param.getPackageName() 过滤目标应用后执行 hook。
 */
public class MainHook extends XposedModule {

    public MainHook(XposedModuleContext context) {
        super(context);
        Log.i("Ria", "Ria module loaded");
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);
        Log.i("Ria", "loaded in " + param.getPackageName());
    }
}
