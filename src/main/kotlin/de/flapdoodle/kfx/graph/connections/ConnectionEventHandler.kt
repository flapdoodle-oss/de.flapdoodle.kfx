package de.flapdoodle.kfx.graph.connections

import de.flapdoodle.kfx.graph.nodes.Connector

interface ConnectionEventHandler {
    fun isConnectable(matching: Connector): Boolean
    fun connectableTo(source: Connector, destination: Connector): Boolean
    fun onConnect(source: Connector, destination: Connector)
    fun onSelect(selection: Connections.Connection)
}