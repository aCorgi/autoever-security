package com.task.autoeversecurity.domain.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    @Column(nullable = false, length = 200)
    var city: String,
    @Column(nullable = false, length = 200)
    var district: String,
    @Column(nullable = false, length = 200)
    var town: String,
)
