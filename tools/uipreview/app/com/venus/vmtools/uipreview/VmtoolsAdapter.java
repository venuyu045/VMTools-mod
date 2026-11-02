package com.venus.vmtools.uipreview;

import com.venus.vmtools.VMToolsClient;
import com.venus.vmtools.feature.waypoint.WaypointGroup;
import com.venus.vmtools.gui.AutoEscapeScreen;
import com.venus.vmtools.gui.SettingsScreen;
import com.venus.vmtools.gui.WaypointScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import uipreview.UiPreviewAdapter;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VMTools 的 UI 预览适配器 —— 预览器内核里<b>唯一</b>知道这个 mod 的地方。
 *
 * <p>与 VMFashion 那份适配器结构完全一致，区别只在：模组 id、要预览的界面、
 * 造什么假数据、有哪些场景。
 */
public final class VmtoolsAdapter implements UiPreviewAdapter {

    @Override
    public String modId() {
        return "vmtools";
    }

    @Override
    public String displayName() {
        return "VMTools · 路径点管理";
    }

    @Override
    public Screen createScreen() {
        return new WaypointScreen();
    }

    @Override
    public Screen createScreen(String scenario) {
        return switch (scenario) {
            case "settings" -> new SettingsScreen(null);
            case "auto_escape" -> new AutoEscapeScreen();
            default -> new WaypointScreen();
        };
    }

    @Override
    public void installFixtures(String scenario, Path workDir) {
        Fixtures.install(scenario);
    }

    @Override
    public Map<String, Scenario> scenarios() {
        Map<String, Scenario> map = new LinkedHashMap<>();
        map.put("default", Scenario.of("default", "默认（3 个分组）"));
        map.put("search", Scenario.of("search", "搜索过滤",
                screen -> typeIntoSearch(screen, "出生")));
        map.put("search_empty", Scenario.of("search_empty", "搜索无结果",
                screen -> typeIntoSearch(screen, "zzz不存在")));
        map.put("empty", Scenario.of("empty", "空列表"));
        map.put("settings", Scenario.of("settings", "设置界面"));
        map.put("auto_escape", Scenario.of("auto_escape", "逃逸小工具"));
        return map;
    }

    @Override
    public Map<String, Object> describeState(Screen screen) {
        Map<String, Object> state = new LinkedHashMap<>();
        int groupCount = 0;
        int waypointCount = 0;
        for (WaypointGroup group : VMToolsClient.getInstance().getWaypointManager().getGroups()) {
            groupCount++;
            waypointCount += group.getWaypointCount();
        }
        state.put("groups", groupCount);
        state.put("waypoints", waypointCount);
        // 调试：渲染顺序与窗口状态（定位"只画出一个分组"这类问题）
        try {
            var orderField = screen.getClass().getDeclaredField("groupRenderOrder");
            orderField.setAccessible(true);
            state.put("renderOrder", String.valueOf(orderField.get(screen)));
            var statesField = screen.getClass().getDeclaredField("windowStates");
            statesField.setAccessible(true);
            Object states = statesField.get(screen);
            Map<String, String> windowInfo = new LinkedHashMap<>();
            if (states instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    Object v = e.getValue();
                    if (v == null) {
                        continue;
                    }
                    int x = (int) readMember(v, "x");
                    int y = (int) readMember(v, "y");
                    boolean expanded = (boolean) readMember(v, "expanded");
                    windowInfo.put(String.valueOf(e.getKey()), "x=" + x + " y=" + y + " exp=" + expanded);
                }
            }
            state.put("windows", windowInfo);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            state.put("debugError", "reflection failed");
        }
        return state;
    }

    /** 通过界面的真实输入链路打字（走 EditBox#charTyped → responder → 过滤）。 */
    private static void typeIntoSearch(Screen screen, String text) {
        EditBox box = findSearchField(screen);
        if (box == null) {
            return;
        }
        box.setFocused(true);
        for (int i = 0; i < text.length(); ) {
            int cp = text.codePointAt(i);
            screen.charTyped(new CharacterEvent(cp));
            i += Character.charCount(cp);
        }
    }

    private static Object readMember(Object target, String name) throws ReflectiveOperationException {
        var field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static EditBox findSearchField(Screen screen) {
        try {
            var field = screen.getClass().getDeclaredField("searchField");
            field.setAccessible(true);
            return (EditBox) field.get(screen);
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }
}
