package com.markit.api.positioning

import com.markit.api.WatermarkAttributes

/**
 *
 * @author Oleg Cheban
 * @since 1.0
 */
interface WatermarkPositionCoordinates {

    fun center(): Coordinates

    fun topLeft(): Coordinates

    fun topRight(): Coordinates

    fun topCenter(): Coordinates

    fun bottomLeft(): Coordinates

    fun bottomRight(): Coordinates
    fun bottomCenter(): Coordinates

    fun tiled(attr: WatermarkAttributes): List<Coordinates>

    data class Coordinates(val x: Int, val y: Int)
}