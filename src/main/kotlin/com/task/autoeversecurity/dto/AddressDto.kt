package com.task.autoeversecurity.dto

import com.task.autoeversecurity.domain.embeddable.Address

data class AddressDto(
    var city: String,
    var district: String,
    var town: String,
) {
    constructor(address: Address) : this(
        city = address.city,
        district = address.district,
        town = address.town,
    )

    fun toEmbeddable(): Address {
        return Address(
            city = city,
            district = district,
            town = town,
        )
    }
}
