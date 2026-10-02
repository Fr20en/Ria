package com.ria.hooks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkBg = Color(0xFF121318)
private val DarkCard = Color(0xFF1E2028)
private val Accent = Color(0xFFB7C0FF)
private val TextMain = Color(0xFFE4E6EE)
private val TextDim = Color(0xFF9AA0B0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RiaApp() }
    }
}

@Composable
fun RiaApp() {
    var tab by remember { mutableIntStateOf(0) }
    val tabs: List<Pair<String, ImageVector>> = listOf(
        "首页" to Icons.Filled.Home,
        "应用" to Icons.Filled.Apps,
        "拓展" to Icons.Filled.Extension,
        "设置" to Icons.Filled.Settings,
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = DarkBg,
            surface = DarkCard,
            primary = Accent,
            onPrimary = Color(0xFF1A1C2A),
            onBackground = TextMain,
            onSurface = TextMain,
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = DarkBg) {
            Column {
                when (tab) {
                    0 -> HomeScreen()
                    1 -> AppsScreen()
                    2 -> ExtensionsScreen()
                    else -> SettingsScreen()
                }
                NavigationBar(containerColor = DarkCard) {
                    tabs.forEachIndexed { i, (label, icon) ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                unselectedIconColor = TextDim,
                                unselectedTextColor = TextDim,
                                indicatorColor = Color(0xFF2A2D3A),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageTitle(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = TextMain,
        modifier = Modifier
            .statusBarsPadding()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
    )
}

@Composable
private fun PlaceholderCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, fontSize = 14.sp, color = TextDim)
        }
    }
}

@Composable
private fun HomeScreen() {
    Column(Modifier.fillMaxSize()) {
        PageTitle("Ria")
        Spacer(Modifier.height(8.dp))
        PlaceholderCard(
            "模块状态",
            "模块已安装。在 LSPosed 中启用 Ria 并勾选目标应用的作用域。",
        )
        PlaceholderCard(
            "Hook 模板",
            "空模板骨架。hook 逻辑待接入：入口 com.ria.hooks.MainHook。",
        )
    }
}

@Composable
private fun AppsScreen() {
    Column(Modifier.fillMaxSize()) {
        PageTitle("适配应用")
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(DarkCard, RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = TextDim, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(12.dp))
            Text("搜索应用", fontSize = 15.sp, color = TextDim)
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("等待 hook 模板接入后在此配置目标应用", fontSize = 14.sp, color = TextDim)
        }
    }
}

@Composable
private fun ExtensionsScreen() {
    Column(Modifier.fillMaxSize()) {
        PageTitle("拓展")
        Spacer(Modifier.height(8.dp))
        PlaceholderCard("暂无拓展", "hook 模板接入后，拓展功能将显示在这里。")
    }
}

@Composable
private fun SettingsScreen() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        PageTitle("设置")
        Spacer(Modifier.height(8.dp))
        PlaceholderCard("设置项待接入", "hook 模板接入后，这里提供各功能的开关与参数配置。")
    }
}
