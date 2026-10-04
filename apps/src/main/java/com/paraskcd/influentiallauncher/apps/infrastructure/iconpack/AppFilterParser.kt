// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.infrastructure.iconpack

import org.xmlpull.v1.XmlPullParser

object AppFilterParser {
    private const val Item = "item"
    private const val Calendar = "calendar"
    private const val IconBack = "iconback"
    private const val IconMask = "iconmask"
    private const val IconUpon = "iconupon"
    private const val Scale = "scale"
    private const val Component = "component"
    private const val Drawable = "drawable"
    private const val Prefix = "prefix"
    private const val Factor = "factor"
    private const val Image = "img"
    private const val Wrapper = "ComponentInfo"
    private val Braces = charArrayOf('{', '}', ' ')

    fun parse(parser: XmlPullParser): IconPackMap {
        val items = mutableMapOf<String, String>()
        val calendars = mutableMapOf<String, String>()
        val backs = mutableListOf<String>()
        var mask: String? = null
        var upon: String? = null
        var scale = 1f
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) continue
            when (parser.name) {
                Item -> entry(parser, Drawable)?.let { (component, drawable) -> items.putIfAbsent(component, drawable) }
                Calendar -> entry(parser, Prefix)?.let { (component, prefix) -> calendars.putIfAbsent(component, prefix) }
                IconBack -> backs += images(parser)
                IconMask -> mask = images(parser).firstOrNull() ?: mask
                IconUpon -> upon = images(parser).firstOrNull() ?: upon
                Scale -> scale = parser.getAttributeValue(null, Factor)?.toFloatOrNull() ?: scale
            }
        }
        return IconPackMap(items, calendars, backs, mask, upon, scale)
    }

    fun component(raw: String): String? {
        val cleaned = raw.trim().removePrefix(Wrapper).trim(*Braces)
        if (cleaned.startsWith(':')) return null
        val packageName = cleaned.substringBefore('/', "").takeIf { it.isNotEmpty() } ?: return null
        val activity = cleaned.substringAfter('/').takeIf { it.isNotEmpty() } ?: return null
        return if (activity.startsWith('.')) "$packageName/$packageName$activity" else "$packageName/$activity"
    }

    private fun entry(parser: XmlPullParser, valueAttribute: String): Pair<String, String>? {
        val component = parser.getAttributeValue(null, Component)?.let(::component) ?: return null
        val value = parser.getAttributeValue(null, valueAttribute)?.takeIf { it.isNotBlank() } ?: return null
        return component to value
    }

    private fun images(parser: XmlPullParser): List<String> =
        (0 until parser.attributeCount)
            .filter { parser.getAttributeName(it).startsWith(Image) }
            .map { parser.getAttributeValue(it) }
            .filter { it.isNotBlank() }
}
