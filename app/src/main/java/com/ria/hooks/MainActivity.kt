package com.ria.hooks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import java.io.File

// ---------------------------------------------------------------- 主题

private val ActiveGreen = Color(0xFF43A047)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFB7C0FF),
    onPrimary = Color(0xFF1A1C2A),
    background = Color(0xFF121318),
    surface = Color(0xFF1E2028),
    onBackground = Color(0xFFE4E6EE),
    onSurface = Color(0xFFE4E6EE),
    onSurfaceVariant = Color(0xFF9AA0B0),
    outlineVariant = Color(0xFF2A2D3A),
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF40498C),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF6F7FB),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1C24),
    onSurface = Color(0xFF1A1C24),
    onSurfaceVariant = Color(0xFF6B7080),
    outlineVariant = Color(0xFFE2E4EC),
)

// ---------------------------------------------------------------- 状态与设置存储

object ActiveState {
    private const val MAX_AGE_MS = 48 * 3600_000L

    /** MainHook 被注入自身进程时会写 cache/ria_active，48 小时内视为激活。 */
    fun isActive(ctx: Context): Boolean {
        val flag = File(ctx.applicationInfo.dataDir, "cache/ria_active")
        if (!flag.isFile) return false
        val age = System.currentTimeMillis() - flag.lastModified()
        return age in 0..MAX_AGE_MS
    }
}

object SettingsStore {
    private const val PREFS = "ria_settings"
    private const val KEY_THEME = "theme_mode" // 0 跟随系统 / 1 浅色 / 2 深色

    fun themeMode(ctx: Context): Int =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_THEME, 0)

    fun setThemeMode(ctx: Context, mode: Int) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_THEME, mode).commit()
    }
}

// ---------------------------------------------------------------- 入口与导航

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RiaApp() }
    }
}

@Composable
fun RiaApp() {
    val ctx = LocalContext.current
    var themeMode by remember { mutableIntStateOf(SettingsStore.themeMode(ctx)) }
    val darkTheme = when (themeMode) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }

    MaterialTheme(colorScheme = if (darkTheme) DarkScheme else LightScheme) {
        val cs = MaterialTheme.colorScheme
        Surface(Modifier.fillMaxSize(), color = cs.background) {
            var tab by remember { mutableIntStateOf(0) }
            val tabs: List<Pair<String, ImageVector>> = listOf(
                "首页" to Icons.Filled.Home,
                "应用" to Icons.Filled.Apps,
                "拓展" to Icons.Filled.Extension,
                "设置" to Icons.Filled.Settings,
            )
            Column {
                when (tab) {
                    0 -> HomeScreen()
                    1 -> AppsScreen()
                    2 -> ExtensionsScreen()
                    else -> SettingsScreen(
                        themeMode = themeMode,
                        onThemeChanged = { mode ->
                            themeMode = mode
                            SettingsStore.setThemeMode(ctx, mode)
                        },
                    )
                }
                NavigationBar(containerColor = cs.surface) {
                    tabs.forEachIndexed { i, (label, icon) ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------- 通用组件

@Composable
private fun PageTitle(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .statusBarsPadding()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
    )
}

@Composable
private fun CardContainer(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        content()
    }
}

@Composable
private fun CardDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ClickRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
    }
}

// ---------------------------------------------------------------- 首页

@Composable
private fun HomeScreen() {
    val ctx = LocalContext.current
    val active = remember { ActiveState.isActive(ctx) }
    val version = remember {
        try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "-"
        } catch (e: Exception) {
            "-"
        }
    }

    Column(Modifier.fillMaxSize()) {
        PageTitle("Ria")
        Spacer(Modifier.height(8.dp))

        CardContainer {
            Row(
                Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(44.dp)
                        .background(
                            if (active) ActiveGreen.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (active) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (active) ActiveGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Spacer(Modifier.size(14.dp))
                Column {
                    Text(
                        if (active) "模块已激活" else "模块未激活",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        if (active) "LSPosed 已加载 hook 入口（48 小时内检测到）"
                        else "请在 LSPosed 中启用 Ria，并在作用域中勾选目标应用与 Ria 自身",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            CardDivider()
            InfoRow("版本", version)
            CardDivider()
            InfoRow("Hook API", "libxposed API 102")
            CardDivider()
            InfoRow("包名", "com.ria.hooks")
        }

        CardContainer {
            Column(Modifier.padding(20.dp)) {
                Text("Hook 模板", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Text(
                    "空模板骨架，hook 逻辑待接入。入口：com.ria.hooks.MainHook（libxposed API 102）。",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ---------------------------------------------------------------- 应用 / 拓展

@Composable
private fun AppsScreen() {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize()) {
        PageTitle("适配应用")
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(cs.surface, RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = cs.onSurfaceVariant, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(12.dp))
            Text("搜索应用", fontSize = 15.sp, color = cs.onSurfaceVariant)
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("等待 hook 模板接入后在此配置目标应用", fontSize = 14.sp, color = cs.onSurfaceVariant)
        }
    }
}

@Composable
private fun ExtensionsScreen() {
    Column(Modifier.fillMaxSize()) {
        PageTitle("拓展")
        Spacer(Modifier.height(8.dp))
        CardContainer {
            Column(Modifier.padding(20.dp)) {
                Text("暂无拓展", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Text("hook 模板接入后，拓展功能将显示在这里。", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------- 设置

@Composable
private fun SettingsScreen(themeMode: Int, onThemeChanged: (Int) -> Unit) {
    val ctx = LocalContext.current
    val version = remember {
        try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "-"
        } catch (e: Exception) {
            "-"
        }
    }

    Column(Modifier.fillMaxSize()) {
        PageTitle("设置")
        Spacer(Modifier.height(8.dp))

        Text(
            "外观",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 22.dp, top = 8.dp),
        )
        CardContainer {
            ThemeOptionRow("跟随系统", 0, themeMode, onThemeChanged)
            CardDivider()
            ThemeOptionRow("浅色", 1, themeMode, onThemeChanged)
            CardDivider()
            ThemeOptionRow("深色", 2, themeMode, onThemeChanged)
        }

        Text(
            "关于",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 22.dp, top = 8.dp),
        )
        CardContainer {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(14.dp))
                Text("版本", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Text(version, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            CardDivider()
            ClickRow(
                icon = Icons.Filled.OpenInNew,
                title = "GitHub 仓库",
                subtitle = "github.com/Fr20en/Ria",
                onClick = {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Fr20en/Ria")))
                },
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(label: String, mode: Int, current: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onSelect(mode) }
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        RadioButton(selected = current == mode, onClick = { onSelect(mode) })
    }
}
