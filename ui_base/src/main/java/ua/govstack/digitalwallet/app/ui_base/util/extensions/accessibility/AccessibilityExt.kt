package ua.govstack.digitalwallet.app.ui_base.util.extensions.accessibility

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.isTalkBackEnabled


private val announcerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
private val lastMessageMap = mutableMapOf<View, String?>()
private val jobMap = mutableMapOf<View, Job>()

fun View.accessibilityAnnouncer(
    message: String,
    delayMillis: Long = 500L
) {
    if (lastMessageMap[this] == message) return
    lastMessageMap[this] = message

    jobMap[this]?.cancel()

    jobMap[this] = announcerScope.launch {
        delay(delayMillis)
        announceForAccessibility(message)
    }
}

fun View.setAccessibilityRole(roleDescription: String) {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.roleDescription = roleDescription
        }
    })
}

fun View.setAccessibilitySelectedState(isSelected: Boolean) {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.isSelected = isSelected
        }
    })
}

fun View.setAccessibilityStateDescription(isSelected: Boolean) {
    val selectedText = context.getString(R.string.accessibility_button_state_selected)
    val unselectedText = context.getString(R.string.accessibility_button_state_unselected)
    ViewCompat.setStateDescription(this, if (isSelected) selectedText else unselectedText)
}

fun View.setupCustomAccessibility(
    isSelected: Boolean? = null,
    roleDescription: String? = null
) {
    this.applyAccessibilityDelegate { info ->
        isSelected?.let { info.isSelected = it }
        roleDescription?.let { info.roleDescription = it }
        info.unwrap().setCollectionItemInfo(null)
    }
}

fun RecyclerView.disableGridAccessibility() {
    this.applyAccessibilityDelegate { info ->
        info.setCollectionInfo(null)
    }
}

private fun View.applyAccessibilityDelegate(
    block: (info: AccessibilityNodeInfoCompat) -> Unit
) {
    if (context.isTalkBackEnabled()) {
        ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                block(info)
            }
        })
    }
}

fun View.setAccessibilityExpandState(isExpanded: Boolean) {
    val expandedText = context.getString(R.string.accessibility_expand)
    val collapsedText = context.getString(R.string.accessibility_collapse)
    ViewCompat.setStateDescription(this, if (isExpanded) expandedText else collapsedText)
}

fun View.requestAccessibilityFocus() {
    if (context.isTalkBackEnabled()) {
        viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver?.removeOnGlobalLayoutListener(this)
                isFocusable = true
                isFocusableInTouchMode = true
                requestFocus()
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
            }
        })
    }
}

fun View.handleAccessibilityFocus(action: () -> Unit) {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun sendAccessibilityEventUnchecked(host: View, event: AccessibilityEvent) {
            super.sendAccessibilityEventUnchecked(host, event)
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                action()
            }
        }
    })
}

fun View.setChildrenImportantForAccessibility(mode: Int) {
    if (this !is ViewGroup) return
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        child.importantForAccessibility = mode
        child.setChildrenImportantForAccessibility(mode)
    }
}

fun ViewPager2.setupAccessibilityFocusSync() {
    val recyclerView = this.getChildAt(0) as? RecyclerView ?: return

    var lastCenteredPosition = RecyclerView.NO_POSITION

    recyclerView.accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun onRequestSendAccessibilityEvent(
            host: ViewGroup,
            child: View,
            event: AccessibilityEvent
        ): Boolean {
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                val position = recyclerView.getChildAdapterPosition(child)
                if (position == RecyclerView.NO_POSITION) {
                    return super.onRequestSendAccessibilityEvent(host, child, event)
                }

                if (position != lastCenteredPosition) {
                    lastCenteredPosition = position

                    this@setupAccessibilityFocusSync.setCurrentItem(position, true)

                    recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            recyclerView.smoothScrollToPosition(position)
                            child.requestFocus()
                        }
                    })
                }
            }

            return super.onRequestSendAccessibilityEvent(host, child, event)
        }
    }
}

fun String.removeEmojis(): String =
    replace(
        Regex("[\\p{So}\\p{Sk}\\p{Cn}\\p{Cs}\\x{1F1E6}-\\x{1F1FF}\\x{1F300}-" +
                "\\x{1F6FF}\\x{1F900}-\\x{1F9FF}\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}]"),
        ""
    ).trim()

fun String.splitTextWithLink(): Pair<String, String> {
    val linkPlaceholder = "[\\({][^)}]+[\\)}]".toRegex()
    val sentences = this.split(Regex("(?<=[.!?])\\s+"))
    val lastWithLink = sentences.findLast { it.contains(linkPlaceholder) }

    return if (lastWithLink != null) {
        val mainPart = this.substringBefore(lastWithLink).trim()
        mainPart to lastWithLink.trim()
    } else {
        this to ""
    }
}

@Composable
fun <T> T.resolveContentDescription(
    getCode: (T) -> String,
    getAccessibilityDescription: (T) -> String?
): String? {
    val accessibilityDescription = getAccessibilityDescription(this)
    val code = getCode(this)
    val descriptionResId = DiiaResourceIcon.getContentDescription(code)

    return when {
        accessibilityDescription != null -> accessibilityDescription
        descriptionResId == R.string.not_handled_icon -> null
        else -> stringResource(id = descriptionResId)
    }
}