package nl.streaem.assessment.model

import nl.streaem.assessment.helpers.IdGenerator

data class Product(
    val id: Long? = IdGenerator.nextId(),
    var name: String? = null,
    var description: String? = null,
    var price: Double? = null,
    var category: String? = null,
    var stockLevel: Int = 0,
)