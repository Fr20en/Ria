package com.ria.hooks.app;

import com.ria.hooks.HookHelper;

import io.github.libxposed.api.XposedModule;

/**
 * 漫客栈 3.9.0 (com.xmtj.mkz)：VIP 永久解锁 + 全广告屏蔽（含净化卡）。
 * 逻辑来自实测模板：目标类位于 classes2-9.dex（360 加固，未抽取方法体）。
 * 注意：VIP 章节内容若由服务端加密下发，仅去客户端弹窗，图片可能仍不可读。
 */
public final class MkzHooks {

    private static final String TAG = "MkzHook";

    public static void hook(XposedModule mod, ClassLoader cl) {
        HookHelper h = new HookHelper(mod);
        try {
            hookVip(h, cl);
            hookChapterVip(h, cl);
            hookPurify(h, cl);
            hookAd(h, cl);
            mod.log(android.util.Log.INFO, TAG, "all hooks loaded");
        } catch (Throwable t) {
            mod.log(android.util.Log.ERROR, TAG, "init fail: " + t);
        }
    }

    // ── 1. VIP 解锁 ──────────────────────────────────────
    private static void hookVip(HookHelper h, ClassLoader cl) {
        Class<?> fundInfo = h.findClass("com.xmtj.library.base.bean.BaseUserFundInfo", cl);
        if (fundInfo != null) {
            h.hookReturn(h.findMethod(fundInfo, "isVip"), true);
            h.hookReturn(h.findMethod(fundInfo, "getVipEndTime"), 4102444800000L); // 永不过期
            String[] vipMethods = {"isPtGoldVip", "isBlackGoldVip", "isKnight", "isDuke", "isKing"};
            for (String m : vipMethods) {
                h.hookReturn(h.findMethod(fundInfo, m), true);
            }
        }
        Class<?> userMgr = h.findClass("com.xmtj.mkz.business.user.c", cl);
        if (userMgr != null) {
            h.hookReturn(h.findMethod(userMgr, "L"), true); // UserManager 单例委托
        }
    }

    // ── 2. 章节 VIP 门禁清除 ─────────────────────────────
    private static void hookChapterVip(HookHelper h, ClassLoader cl) {
        String[] chapterClasses = {
                "com.xmtj.library.greendao_bean.dependbean.ChapterInfo",
                "com.xmtj.library.greendao_bean.ChapterCacheInfo",
                "com.xmtj.library.base.bean.ComicBean",
        };
        for (String cn : chapterClasses) {
            Class<?> c = h.findClass(cn, cl);
            if (c == null) continue;
            h.hookReturn(h.findMethod(c, "isVip"), false);
            h.hookReturn(h.findMethod(c, "isVipExclusive"), false);
        }
    }

    // ── 3. 去广告：净化卡 ────────────────────────────────
    private static void hookPurify(HookHelper h, ClassLoader cl) {
        Class<?> purify = h.findClass("com.xmtj.library.base.bean.PurifyUseBean", cl);
        if (purify != null) {
            h.hookReturn(h.findMethod(purify, "isAvailable"), true);
            h.hookReturn(h.findMethod(purify, "isExpire"), false);
            h.hookReturn(h.findMethod(purify, "isContainsPuirfyAdType", int.class), true);
        }
        Class<?> fundInfo = h.findClass("com.xmtj.library.base.bean.BaseUserFundInfo", cl);
        if (fundInfo != null) {
            h.hookReturn(h.findMethod(fundInfo, "isPuirfyUseBeanAvailable"), true);
            h.hookReturn(h.findMethod(fundInfo, "isPuirfyUseBeanTypeAvailable", int.class), true);
        }
    }

    // ── 4. 去广告：广告数据源清空 + SDK 加载兜底 ─────────
    private static void hookAd(HookHelper h, ClassLoader cl) {
        Class<?> ds = h.findClass("com.xmtj.library.ad.factory.adfliter.a", cl);
        if (ds != null) {
            h.hookClearListArg(h.findMethod(ds, "a", java.util.List.class));
        }
        Class<?> laf = h.findClass("com.xmtj.library.ad.factory.adload.LoadAdFactory", cl);
        if (laf != null) {
            h.hookAllVoidReturn(laf);
        }
    }
}
