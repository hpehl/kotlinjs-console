package org.wildfly.halos.model

import dev.fritz2.elemento.elements
import dev.fritz2.mvp.Presenter
import dev.fritz2.mvp.View
import org.patternfly.modifier
import org.patternfly.pfContent
import org.patternfly.pfSection

class ManagementModelPresenter : Presenter<ManagementModelView> {
    override val view = ManagementModelView()
}

class ManagementModelView : View {
    override val elements = elements {
        pfSection("light".modifier()) {
            pfContent {
                h1 { +"Management Model" }
                p { +"Not yet implemented" }
            }
        }
    }
}
