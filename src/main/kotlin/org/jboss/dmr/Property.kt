package org.jboss.dmr

data class Property(val name: String, val value: ModelNode) {
    companion object {
        val UNDEFINED = Property("undefined", ModelNode.UNDEFINED)
    }
}
