package com.github.mislavmatijevic.nutrity.core.mapper

/**
 * Generic purpose mapper for mapping objects of different origins into something more usable.
 */
interface GenericMapper<D, E> {
    fun mapEntity(entity: E): D
    fun map(dto: D): E
}