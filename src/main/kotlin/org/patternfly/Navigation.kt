package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import org.jboss.elemento.*
import org.patternfly.ComponentType.Navigation
import org.w3c.dom.*
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.browser.window
import kotlin.collections.set

// ------------------------------------------------------ dsl

fun <T> SidebarTag.pfVerticalNav(block: NavigationTag<T>.() -> Unit = {}) =
    NavigationTag<T>(Orientation.VERTICAL, false, consumer).visit {
        if (this@pfVerticalNav.dark) {
            classes += "dark".modifier()
        }
        block()
    }

fun <T> NavigationTag<T>.pfNavGroup(text: String, block: NavigationGroupTag<T>.() -> Unit) {
    NavigationGroupTag(this.identifier, consumer).visit {
        val titleId = Id.unique("ns")
        aria["labelledby"] = titleId
        h2("nav".component("section", "title")) {
            id = titleId
            +text
        }
        block()
    }
}

fun <T> NavigationTag<T>.pfNavItems(block: NavigationItemsTag<T>.() -> Unit = {}) {
    NavigationItemsTag(this.identifier, "nav".component("list"), consumer).visit {
        block()
    }
}

fun <T> NavigationGroupTag<T>.pfNavItems(block: NavigationItemsTag<T>.() -> Unit = {}) {
    NavigationItemsTag(this.identifier, "nav".component("list"), consumer).visit {
        block()
    }
}

fun <T> UL.pfNavExpandableGroup(text: String, expanded: Boolean = false, block: UL.() -> Unit = {}) {
    li(buildString {
        append("nav".component("item")).append(" ").append("expandable".modifier())
        if (expanded) {
            append(" ").append("expanded".modifier())
        }
    }) {
        a("#", classes = "nav".component("link")) {
            aria["expanded"] = expanded.toString()
            onClickFunction = {
                with(it.target as Element) {
                    pfNav<T>().toggle(this)
                }
            }
            +text
            span("nav".component("toggle")) {
                pfIcon("angle-right".fas())
            }
        }
        section("nav".component("subnav")) {
            hidden = !expanded
            val titleId = Id.unique("ns")
            aria["labelledby"] = titleId
            h2("pf-screen-reader") {
                id = titleId
                +text
            }
            // TODO Replace with NavigationItemsTag("nav".component("simple-list"))
            //  and pass identifier
            ul("nav".component("simple-list")) {
                block()
            }
        }
    }
}

fun <T> NavigationItemsTag<T>.pfNavItem(text: String, item: T) {
    li("nav".component("item")) {
        a(classes = "nav".component("link")) {
            this@pfNavItem.identifier?.let {
                attributes[Dataset.NAVIGATION_ITEM.long] = it(item)
                onClickFunction = {
                    it.target.pfNav<T>().select(item)
                }
            }
            +text
        }
    }
}

// ------------------------------------------------------ tag

class NavigationTag<T>(
    private val orientation: Orientation,
    private val tertiary: Boolean = false,
    consumer: TagConsumer<*>
) : NAV(attributesMapOf("class", "nav".component()), consumer), PatternFlyTag, Ouia {

    private val id: String = Id.unique()
    override val componentType: ComponentType = Navigation

    var identifier: Identifier<T>? = null
        set(value) {
            field = value
            if (value != null) {
                attributes[Dataset.REGISTRY.long] = id
                identifierRegistry[id] = value
            }
        }

    var onSelect: SelectHandler<T>? = null
        set(value) {
            field = value
            if (value != null) {
                val id = Id.unique()
                attributes[Dataset.REGISTRY.long] = id
                selectRegistry[id] = value
            }
        }

    override fun head() {
        if (!tertiary) {
            aria["label"] = "Global"
        }
    }
}

class NavigationGroupTag<T>(internal val identifier: Identifier<T>?, consumer: TagConsumer<*>) :
    SECTION(attributesMapOf("class", "nav".component("section")), consumer)

class NavigationItemsTag<T>(internal val identifier: Identifier<T>?, classes: String, consumer: TagConsumer<*>) :
    UL(attributesMapOf("class", classes), consumer)

// ------------------------------------------------------ component

@Suppress("UNCHECKED_CAST")
fun <T> Document.pfNav(): NavigationComponent<T> {
    val selector = "${Navigation.selector()}[aria-label=Global]"
    return document.querySelector(selector).pfNav()
}

fun <T> EventTarget?.pfNav(): NavigationComponent<T> = (this as Element).pfNav()

fun <T> Element?.pfNav(): NavigationComponent<T> =
    component(this, Navigation, { document.create.nav() }, { it }, ::NavigationComponent)

class NavigationComponent<T>(element: HTMLElement) : PatternFlyComponent<HTMLElement>(element) {

    private val identifier: Identifier<T> by identifier<NavigationComponent<T>, T>()
    private val onSelect: SelectHandler<T>? by selectHandler<NavigationComponent<T>, T>()

    fun autoSelect(createItem: (PopStateEvent) -> T) {
        window.addEventListener("popstate", {
            select(createItem(it as PopStateEvent), false)
        })
    }

    fun select(item: T, fireEvent: Boolean = true) {
        val itemId = identifier(item)

        // first (de)select the items
        val selector = ".${"nav".component("link")}[${Dataset.NAVIGATION_ITEM.long}]"
        val items = element.querySelectorAll(selector)
        items.asList().map { it as HTMLElement }.forEach {
            if (it.dataset[Dataset.NAVIGATION_ITEM.short] == itemId) {
                it.classList += "current".modifier()
                it.aria["current"] = "page"
                if (fireEvent) {
                    onSelect?.let { handler -> handler(item) }
                }
            } else {
                it.classList -= "current".modifier()
                it.aria.remove("current")
            }
        }

        // then (de)select the expandable parents (if any)
        val expandables = element.querySelectorAll(".${"expandable".modifier()}")
        expandables.asList().map { it as Element }.forEach {
            // it = li.pf-c-nav__item.pf-m-expandable
            if (it.querySelector("#$itemId") != null) {
                it.classList += "current".modifier()
                it.firstElementChild?.let { a -> expand(a) }
            } else {
                it.classList -= "current".modifier()
            }
        }
    }

    internal fun toggle(element: Element) {
        if (element.aria["expanded"].toBoolean()) {
            collapse(element)
        } else {
            expand(element)
        }
    }

    private fun collapse(element: Element) {
        val li = element.parentElement
        val section = element.nextElementSibling
        if (li != null && section != null) {
            li.classList -= "expanded".modifier()
            element.aria["expanded"] = false
            section.hidden = true
        }
    }

    private fun expand(element: Element) {
        val li = element.parentElement
        val section = element.nextElementSibling
        if (li != null && section != null) {
            li.classList += "expanded".modifier()
            element.aria["expanded"] = true
            section.removeAttribute("hidden")
        }
    }
}
