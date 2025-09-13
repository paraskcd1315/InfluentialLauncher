package com.paraskcd.influentiallauncher.ui.dialogs

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.paraskcd.influentiallauncher.R
import com.paraskcd.influentiallauncher.data.types.AppMenuAction
import com.paraskcd.influentiallauncher.ui.theme.InfluentialLauncherTheme
import com.paraskcd.influentiallauncher.utls.isWindowBlurSupported
import kotlin.math.max
import kotlin.math.min

object AppContextMenuDialog {
    private var dialog: Dialog? = null
    private var lastAnchor: Rect? = null
    private var lastActions: List<AppMenuAction> = emptyList()

    private const val H_MARGIN = 12
    private const val V_MARGIN = 12
    private const val PREFERRED_BELOW_GAP = 6
    private const val PREFERRED_ABOVE_GAP = 10
    private const val MAX_WIDTH_DP = 220
    private val MENU_SHAPE = RoundedCornerShape(20.dp)

    fun isShowing(): Boolean = dialog?.isShowing == true

    fun close() {
        dialog?.dismiss()
        dialog = null
    }

    fun show(
        context: Context,
        anchorRect: Rect,
        actions: List<AppMenuAction>
    ) {
        val activity = context as? ComponentActivity ?: return
        val supportsBlur = context.isWindowBlurSupported()
        close()
        buildAndShow(activity, anchorRect, actions, supportsBlur, null, null, null)
    }

    fun show(
        context: Context,
        anchorRect: Rect,
        appLabel: String,
        appIcon: Drawable?,
        onOpenApp: () -> Unit,
        actions: List<AppMenuAction>
    ) {
        val activity = context as? ComponentActivity ?: return
        val supportsBlur = context.isWindowBlurSupported()
        lastAnchor = anchorRect
        lastActions = actions
        close()
        buildAndShow(
            activity = activity,
            anchorRect = anchorRect,
            actions = actions,
            supportsBlur = supportsBlur,
            headerLabel = appLabel,
            headerIcon = appIcon,
            onOpenHeader = onOpenApp
        )
    }

    private fun buildAndShow(
        activity: ComponentActivity,
        anchorRect: Rect,
        actions: List<AppMenuAction>,
        supportsBlur: Boolean,
        headerLabel: String?,
        headerIcon: Drawable?,
        onOpenHeader: (() -> Unit)?
    ) {
        val dlg = Dialog(activity, R.style.Theme_DockWindow)
        dialog = dlg

        val compose = ComposeView(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewTreeLifecycleOwner(activity)
            setViewTreeViewModelStoreOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)
            setContent {
                InfluentialLauncherTheme {
                    MenuContent(
                        actions = actions,
                        supportsBlur = supportsBlur,
                        headerLabel = headerLabel,
                        headerIcon = headerIcon,
                        onOpenHeader = {
                            onOpenHeader?.invoke()
                            close()
                        },
                        onDismiss = { close() }
                    )
                }
            }
        }

        dlg.setContentView(compose)

        dlg.setCanceledOnTouchOutside(true)
        dlg.setOnDismissListener { if (dialog == dlg) dialog = null }

        val win = dlg.window ?: return
        win.setBackgroundDrawableResource(R.drawable.dock_window_bg)
        win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        win.setGravity(Gravity.TOP or Gravity.START)
        win.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Ocultar fuera de pantalla antes de medir
        win.attributes = win.attributes.apply {
            x = -10000
            y = -10000
        }
        win.decorView.alpha = 0f

        var positioned = false
        compose.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (positioned) return
                    if (compose.width > 0 && compose.height > 0) {
                        positioned = true
                        compose.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        positionWindow(activity, win, anchorRect)
                        fadeIn(win)
                    }
                }
            }
        )

        // Importante: llamar show() para que se mida
        dlg.show()
    }

    private fun fadeIn(win: Window) {
        val view = win.decorView
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 120
            addUpdateListener { view.alpha = it.animatedValue as Float }
            start()
        }
    }

    private fun positionWindow(
        activity: ComponentActivity,
        win: Window,
        anchor: Rect
    ) {
        val decor = win.decorView
        val dm: DisplayMetrics = activity.resources.displayMetrics
        val width = decor.measuredWidth
        val height = decor.measuredHeight

        val insets = ViewCompat.getRootWindowInsets(activity.window?.decorView ?: decor)
        val statusBar = insets
            ?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars())
            ?.top ?: 0
        val navBottom = insets
            ?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())
            ?.bottom ?: 0

        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels

        // X centrado respecto al ancla, dentro de margen horizontal
        var x = anchor.centerX() - width / 2
        x = max(H_MARGIN, min(x, screenWidth - width - H_MARGIN))

        val spaceAbove = anchor.top - statusBar
        val spaceBelow = (screenHeight - navBottom) - anchor.bottom
        val wantAbove = spaceAbove >= height + PREFERRED_ABOVE_GAP || spaceAbove > spaceBelow

        val y = if (wantAbove) {
            (anchor.top - height - PREFERRED_ABOVE_GAP).coerceAtLeast(statusBar + V_MARGIN)
        } else {
            (anchor.bottom + PREFERRED_BELOW_GAP)
                .coerceAtMost(screenHeight - navBottom - height - V_MARGIN)
        }

        win.attributes = win.attributes.apply {
            this.x = x
            this.y = y
        }
    }

    @Composable
    private fun MenuContent(
        actions: List<AppMenuAction>,
        supportsBlur: Boolean,
        headerLabel: String?,
        headerIcon: Drawable?,
        onOpenHeader: () -> Unit,
        onDismiss: () -> Unit
    ) {
        val bg = if (supportsBlur)
            MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
        else
            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)

        Box(
            modifier = Modifier
                .widthIn(max = MAX_WIDTH_DP.dp)
                .clip(MENU_SHAPE)
                .background(bg, MENU_SHAPE)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.10f),
                    MENU_SHAPE
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                if (headerLabel != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onOpenHeader() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        if (headerIcon != null) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = rememberDrawablePainter(headerIcon),
                                    contentDescription = headerLabel,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(Modifier.size(12.dp))
                        }
                        Text(
                            headerLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                    )
                }
                actions.forEachIndexed { idx, action ->
                    MenuItem(
                        label = action.label,
                        destructive = action.destructive,
                        onClick = {
                            close()
                            action.onClick()
                        }
                    )
                    if (idx != actions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MenuItem(
        label: String,
        destructive: Boolean,
        onClick: () -> Unit
    ) {
        val contentColor = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        val bgPressed = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}