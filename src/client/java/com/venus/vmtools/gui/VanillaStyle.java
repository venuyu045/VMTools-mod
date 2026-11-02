package com.venus.vmtools.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Minecraft <b>原版容器界面</b>的绘制原语（面板 / 物品格 / 凸起凹陷 / 双线分隔）。
 *
 * <p>画法全部从客户端 jar 的
 * {@code assets/minecraft/textures/gui/container/inventory.png} 里<b>逐像素量出来</b>的，
 * 不是"看着像"：
 * <ul>
 *   <li>面板：{@code #C6C6C6} 底 + 1px 纯黑外框 + 左/上 2px 纯白高光 + 右/下 2px
 *       {@code #555555} 阴影，四角各留 1px 空 → 原版那种"像素圆角"；</li>
 *   <li>物品格：1px {@code #373737} 边框 + {@code #8B8B8B} 内底 + 右/底内侧各 1px 纯白，
 *       那两道白边是"凹进去"的来源；</li>
 *   <li>凸起 / 凹陷：原版 GUI 从不靠颜色表示状态，只靠"光从左上打来"——
 *       凸起是左上白右下暗，凹陷反过来。</li>
 * </ul>
 *
 * <p>放在 {@code gui} 包里而不是内联进 {@code WaypointScreen}：路径点界面、右键菜单、
 * 设置界面都要用同一套画法，各写一份必然会漂移。
 */
public final class VanillaStyle {

    // ---- 原版色板（数值来自 inventory.png 逐像素采样）----
    /** 面板主底色。 */
    public static final int PANEL_COLOR = 0xFFC6C6C6;
    /** 面板外框（1px 纯黑）。 */
    public static final int PANEL_BORDER = 0xFF000000;
    /** 面板内侧高光（左/上 2px 白）。 */
    public static final int PANEL_HIGHLIGHT = 0xFFFFFFFF;
    /** 面板右下阴影（2px #555555）。 */
    public static final int PANEL_SHADOW = 0xFF555555;
    /** 槽位内底。 */
    public static final int SLOT_COLOR = 0xFF8B8B8B;
    /** 槽位边框。 */
    public static final int SLOT_BORDER = 0xFF373737;
    /** 浅底上的正文色（原版 #404040）。 */
    public static final int TEXT_COLOR = 0xFF404040;
    /** 浅底上的次要文字（压在 #8B8B8B 上要够暗）。 */
    public static final int MUTED_COLOR = 0xFF5A5A5A;
    /** 悬停时略亮的底色。 */
    public static final int HOVER_COLOR = 0xFFD6D6D6;
    /** 选中/强调（原版不用彩色描边，用更深一档表示）。 */
    public static final int ACCENT_COLOR = 0xFF373737;
    /** 成功/已启用：深绿。 */
    public static final int SUCCESS_COLOR = 0xFF2E7D32;
    /** 危险/删除：深红。 */
    public static final int DANGER_COLOR = 0xFFB71C1C;
    /** 滚动条滑块（原版 widget/scroller.png 是纯 #C0C0C0）。 */
    public static final int SCROLLBAR_COLOR = 0xFFC0C0C0;
    /** 滚动条轨道（原版 scroller_background.png 是纯黑）。 */
    public static final int SCROLLBAR_BG_COLOR = 0xFF000000;
    /** 拖拽手柄的六个点。 */
    public static final int DRAG_HANDLE_COLOR = 0xFF5A5A5A;
    /** 全屏遮罩。 */
    public static final int OVERLAY_COLOR = 0xB0101010;

    private VanillaStyle() {
    }

    /** 给颜色乘一个透明度系数。 */
    public static int withAlpha(int color, float factor) {
        if (factor >= 1.0f) {
            return color;
        }
        if (factor <= 0.0f) {
            return color & 0x00FFFFFF;
        }
        int alpha = (int) (((color >>> 24) & 0xFF) * factor) & 0xFF;
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    /** 原版容器面板。 */
    public static void panel(GuiGraphicsExtractor context, int x, int y, int w, int h) {
        if (w <= 4 || h <= 4) {
            return;
        }
        context.fill(x, y, x + w, y + h, PANEL_COLOR);
        context.fill(x + 1, y + 1, x + w - 1, y + 3, PANEL_HIGHLIGHT);
        context.fill(x + 1, y + 1, x + 3, y + h - 1, PANEL_HIGHLIGHT);
        context.fill(x + 1, y + h - 3, x + w - 1, y + h - 1, PANEL_SHADOW);
        context.fill(x + w - 3, y + 1, x + w - 1, y + h - 1, PANEL_SHADOW);
        context.fill(x + 1, y, x + w - 1, y + 1, PANEL_BORDER);
        context.fill(x + 1, y + h - 1, x + w - 1, y + h, PANEL_BORDER);
        context.fill(x, y + 1, x + 1, y + h - 1, PANEL_BORDER);
        context.fill(x + w - 1, y + 1, x + w, y + h - 1, PANEL_BORDER);
    }

    /** 原版物品格（凹槽）。 */
    public static void slot(GuiGraphicsExtractor context, int x, int y, int w, int h) {
        if (w <= 2 || h <= 2) {
            return;
        }
        context.fill(x, y, x + w, y + h, SLOT_BORDER);
        context.fill(x + 1, y + 1, x + w - 1, y + h - 1, SLOT_COLOR);
        context.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, PANEL_HIGHLIGHT);
        context.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, PANEL_HIGHLIGHT);
    }

    /**
     * 原版凸起 / 凹陷块。
     *
     * @param sunken {@code true} 画成凹进去的（选中/按下态）
     */
    public static void bevel(GuiGraphicsExtractor context, int x, int y, int w, int h,
                             int fillColor, boolean sunken) {
        if (w <= 1 || h <= 1) {
            return;
        }
        context.fill(x, y, x + w, y + h, fillColor);
        if (sunken) {
            context.fill(x, y, x + w, y + 1, PANEL_SHADOW);
            context.fill(x, y, x + 1, y + h, PANEL_SHADOW);
            context.fill(x, y + h - 1, x + w, y + h, PANEL_HIGHLIGHT);
            context.fill(x + w - 1, y, x + w, y + h, PANEL_HIGHLIGHT);
        } else {
            context.fill(x, y, x + w, y + 1, PANEL_HIGHLIGHT);
            context.fill(x, y, x + 1, y + h, PANEL_HIGHLIGHT);
            context.fill(x, y + h - 1, x + w, y + h, PANEL_SHADOW);
            context.fill(x + w - 1, y, x + w, y + h, PANEL_SHADOW);
        }
    }

    /** 原版"凸起分隔线"：1px 深灰 + 1px 白。 */
    public static void separator(GuiGraphicsExtractor context, int x0, int x1, int y) {
        context.fill(x0, y, x1, y + 1, PANEL_SHADOW);
        context.fill(x0, y + 1, x1, y + 2, PANEL_HIGHLIGHT);
    }
}
