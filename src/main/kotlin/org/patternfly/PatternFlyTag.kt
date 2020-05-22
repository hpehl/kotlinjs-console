package org.patternfly

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.visitTag
import kotlinx.html.visitTagAndFinalize
import org.patternfly.Data.COMPONENT_TYPE

internal fun <T : PatternFlyTag> T.visit(block: T.() -> Unit) = visitTag {
    setupTag(this, block)
}

internal fun <T : PatternFlyTag, R> T.visitAndFinalize(consumer: TagConsumer<R>, block: T.() -> Unit): R =
    visitTagAndFinalize(consumer) {
        setupTag(this, block)
    }

private fun <T : PatternFlyTag> setupTag(tag: T, block: T.() -> Unit) {
    if (tag is Ouia) {
        setOuiaType(tag)
    }
    tag.attributes[COMPONENT_TYPE] = tag.componentType.id
    tag.head()
    tag.block()
    tag.tail()
}

/** Holds information for building PatternFly components as part of the HTML DSL */
internal interface PatternFlyTag : Tag {

    val componentType: ComponentType
    fun head() = Unit
    fun tail() = Unit
}