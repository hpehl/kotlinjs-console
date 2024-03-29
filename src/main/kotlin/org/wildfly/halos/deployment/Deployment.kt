package org.wildfly.halos.deployment

import dev.fritz2.elemento.elements
import dev.fritz2.mvp.Presenter
import dev.fritz2.mvp.View
import org.patternfly.modifier
import org.patternfly.pfContent
import org.patternfly.pfSection

class DeploymentPresenter : Presenter<DeploymentView> {
    override val view = DeploymentView()
}

class DeploymentView : View {
    override val elements = elements {
        pfSection("light".modifier()) {
            pfContent {
                h1 { +"Deployment" }
                p { +"Not yet implemented" }
            }
        }
    }
}
