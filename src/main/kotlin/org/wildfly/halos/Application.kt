package org.wildfly.halos

import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.img
import org.patternfly.*
import org.w3c.dom.HTMLElement
import org.wildfly.halos.server.ServerPresenter
import kotlin.browser.document

object Application {

    fun skeleton(): HTMLElement = document.create.pfPage {
        pfHeader {
            pfBrand {
                pfBrandLink("#${ServerPresenter.TOKEN}") {
                    img(src = "/halos-white.svg", classes = "hal-logo") {
                        classes += "brand".component()
                    }
                }
            }
            pfHeaderTools {
                div("toolbar".layout()) {

                }
            }
        }
        pfSidebar {
            pfVerticalNav {
                pfNavItems {
                    pfNavItem(NavigationItem("#server", "Server"))
                    pfNavItem(NavigationItem("#mm", "Management Model"))
                }
            }
        }
        pfMain(Ids.MAIN)
    }
}