package com.venus.vmtools.uipreview;

import com.venus.vmtools.VMToolsClient;
import com.venus.vmtools.feature.waypoint.UIState;
import com.venus.vmtools.feature.waypoint.Waypoint;
import com.venus.vmtools.feature.waypoint.WaypointColor;
import com.venus.vmtools.feature.waypoint.WaypointGroup;
import com.venus.vmtools.feature.waypoint.WaypointManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 预览器的测试数据 —— 造一组"看起来像真玩家在用"的路径点。
 *
 * <p>{@code Waypoint} / {@code WaypointGroup} / {@code WaypointColor} 都是纯数据类，
 * <b>直接用生产代码的</b>，不写替身 —— 这样"分组折叠后怎么画""颜色标签对不对"
 * 验证的就是真实行为。只有 {@link WaypointManager}（要读磁盘）和
 * {@link UIState}（要 FabricLoader）是替身。
 *
 * <p>数据刻意做成"展开 + 折叠混合"：3 个分组里有一个是折叠的，
 * 这样一张图就能同时看到两种状态的渲染。
 */
public final class Fixtures {

    private Fixtures() {
    }

    /**
     * 按场景灌数据。
     *
     * @param scenario 场景名；{@code "empty"} 表示一个路径点都没有
     */
    public static void install(String scenario) {
        // 场景之间必须隔离：UIState 记着窗口位置/展开状态，不清会串到下一个场景
        UIState.resetForPreview();

        WaypointManager manager = VMToolsClient.getInstance().getWaypointManager();
        if ("empty".equals(scenario)) {
            manager.replaceAll(List.of());
            return;
        }

        List<WaypointGroup> groups = new ArrayList<>();

        WaypointGroup main = new WaypointGroup("主城");
        main.setColor(WaypointColor.GREEN);
        main.addWaypoint(new Waypoint("出生点", "/tp 0 64 0", null));
        main.addWaypoint(new Waypoint("铁匠铺", "/tp 12 65 -3", null));
        main.addWaypoint(new Waypoint("传送大厅", "/tp -8 70 15", null));
        groups.add(main);

        WaypointGroup mine = new WaypointGroup("矿洞");
        mine.setColor(WaypointColor.ORANGE);
        mine.addWaypoint(new Waypoint("钻石层", "/tp 120 -54 88", null));
        mine.addWaypoint(new Waypoint("下界入口", "/tp 64 32 -12", null));
        groups.add(mine);

        WaypointGroup farm = new WaypointGroup("农场");
        farm.setColor(WaypointColor.YELLOW);
        // 故意折叠一个分组：一张图里同时验证展开态和折叠态的渲染
        farm.setExpanded(false);
        farm.addWaypoint(new Waypoint("小麦田", "/tp -40 68 200", null));
        farm.addWaypoint(new Waypoint("牧场", "/tp -52 70 215", null));
        groups.add(farm);

        manager.replaceAll(groups);
    }
}
