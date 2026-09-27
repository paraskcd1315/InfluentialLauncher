package com.paraskcd.influentiallauncher.homescreen.domain.usecase

object SlotPlacement {
    fun <T : Any> place(slots: List<T?>, index: Int, item: T): List<T?> {
        val target = index.coerceAtLeast(0)
        val result = slots.toMutableList()
        while (result.size < target) result += null
        when {
            target == result.size -> result += item
            result[target] == null -> result[target] = item
            else -> {
                val gap = (target + 1 until result.size).firstOrNull { result[it] == null }
                if (gap != null) result.removeAt(gap)
                result.add(target, item)
            }
        }
        return result
    }

    fun <T : Any> trimEnd(slots: List<T?>): List<T?> = slots.dropLastWhile { it == null }
}
