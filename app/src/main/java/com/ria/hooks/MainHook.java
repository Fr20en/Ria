package com.ria.hooks;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Ria 空模板入口。
 * hook 模板接入点：在 handleLoadPackage 中按 lpparam.packageName 过滤目标应用后执行 hook。
 */
public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log("Ria: loaded in " + lpparam.packageName);
    }
}
