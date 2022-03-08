package de.flapdoodle.kfx.graph.nodes

fun interface ConnectableConnectors {
    fun filterConnectables(connectors: Iterable<Connector>, start: Connector?): Iterable<Connector>
}