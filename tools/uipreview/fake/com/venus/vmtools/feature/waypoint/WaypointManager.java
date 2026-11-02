package com.venus.vmtools.feature.waypoint;

import java.util.ArrayList;
import java.util.List;

/**
 * UI 预览器专用的 {@code WaypointManager} <b>替身</b>（编译时替换真实类）。
 *
 * <p>真实类要读磁盘上的 {@code waypoints.json}（静态字段还会碰 {@code FabricLoader}）。
 * 预览器不需要持久化，但<b>需要能注入可控数据</b> —— 否则"3 个分组 / 每组几件 / 展开状态"
 * 这些布局问题没法稳定复现。用 {@link #replaceAll} 灌数据，{@link #save} 是空操作。
 */
public class WaypointManager {

    private List<WaypointGroup> groups = new ArrayList<>();

    /** 预览器专用：整体替换分组列表。 */
    public void replaceAll(List<WaypointGroup> newGroups) {
        this.groups = new ArrayList<>(newGroups == null ? List.of() : newGroups);
    }

    public List<WaypointGroup> getGroups() {
        return groups;
    }

    /** 预览器不落盘。 */
    public void save() {
    }

    /** 按 id 找分组；找不到返回 {@code null}（与真实类一致）。 */
    public WaypointGroup findGroup(String groupId) {
        if (groupId == null) {
            return null;
        }
        for (WaypointGroup group : groups) {
            if (groupId.equals(group.getId())) {
                return group;
            }
        }
        return null;
    }

    /** 新增分组（编辑分组界面用）。 */
    public void addGroup(WaypointGroup group) {
        if (group != null) {
            groups.add(group);
        }
    }

    /**
     * 导入分组。
     *
     * <p>真实实现会做去重/合并并返回导入的路径点数量；预览器只需要"能跑通并给出一个合理数字"。
     *
     * @return 导入的路径点总数
     */
    public int importWaypoints(List<WaypointGroup> imported) {
        if (imported == null || imported.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (WaypointGroup group : imported) {
            groups.add(group);
            count += group.getWaypointCount();
        }
        return count;
    }

    public boolean removeGroup(String groupId) {
        return groups.removeIf(group -> group.getId().equals(groupId));
    }

    public boolean removeWaypoint(String groupId, String waypointId) {
        for (WaypointGroup group : groups) {
            if (group.getId().equals(groupId)) {
                return group.removeWaypoint(waypointId);
            }
        }
        return false;
    }

    /** 预览器不真的传送。 */
    public boolean executeTeleport(Waypoint waypoint) {
        return waypoint != null;
    }
}
