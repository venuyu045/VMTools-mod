package com.venus.vmtools.feature.waypoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI 预览器专用的 {@code UIState} <b>替身</b>（编译时替换真实类）。
 *
 * <p>真实类的静态字段要 {@code FabricLoader.getInstance().getConfigDir()}，
 * 预览器里直接抛 {@code ExceptionInInitializerError}。替身放内存，
 * 并额外提供 {@link #resetForPreview()} —— 批量渲染多个场景时，
 * 上一个场景改过的窗口位置/展开状态必须清掉，否则场景之间会串。
 */
public class UIState {

    /** 窗口位置与尺寸（与真实类同名同字段）。 */
    public static class WindowState {
        public int x;
        public int y;
        public boolean expanded;
        public int width = -1;
        public int maxHeight = -1;

        public WindowState() {
        }

        public WindowState(int x, int y, boolean expanded) {
            this.x = x;
            this.y = y;
            this.expanded = expanded;
        }
    }

    private static UIState instance;

    private final Map<String, WindowState> windowStates = new HashMap<>();
    private List<String> groupRenderOrder = new ArrayList<>();

    private UIState() {
    }

    public static UIState load() {
        if (instance == null) {
            instance = new UIState();
        }
        return instance;
    }

    /** 预览器专用：清空所有状态（场景之间隔离）。 */
    public static void resetForPreview() {
        instance = null;
    }

    public void save() {
    }

    /**
     * 取某个分组的窗口状态。
     *
     * <p><b>没有记录时必须返回 {@code null}</b>（与真实类一致）—— 界面靠这个 {@code null}
     * 判断"这是新窗口，要分配默认位置"。早先这里写成 {@code computeIfAbsent}（永远返回对象），
     * 界面就会以为"已有保存位置 (0,0)"，于是所有分组窗口都叠在左上角。
     */
    public WindowState getWindowState(String groupId) {
        return windowStates.get(groupId);
    }

    public void setWindowState(String groupId, int x, int y, boolean expanded, int width, int maxHeight) {
        WindowState state = windowStates.computeIfAbsent(groupId, key -> new WindowState());
        state.x = x;
        state.y = y;
        state.expanded = expanded;
        state.width = width;
        state.maxHeight = maxHeight;
    }

    public void removeWindowState(String groupId) {
        windowStates.remove(groupId);
    }

    public List<String> getGroupRenderOrder() {
        return groupRenderOrder;
    }

    public void setGroupRenderOrder(List<String> order) {
        this.groupRenderOrder = new ArrayList<>(order == null ? List.of() : order);
    }
}
