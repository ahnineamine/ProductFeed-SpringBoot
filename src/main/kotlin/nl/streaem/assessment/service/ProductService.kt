package nl.streaem.assessment.service

import jakarta.annotation.PostConstruct
import nl.streaem.assessment.model.Product
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductService(
    private val restTemplate: RestTemplate,
    @Value("\${external.api.protocol}") private val apiProtocol: String,
    @Value("\${external.api.host}") private val apiHost: String,
    @Value("\${external.api.port}") private val apiPort: String,
    @Value("\${external.api.endpoint}") private val apiEndpoint: String
) {
    private val productFeed = mutableListOf<Product>()

    fun setProductFeed(products: List<Product>) {
        productFeed.clear()
        productFeed.addAll(products)
    }

    @PostConstruct
    fun init() {
        val productList = fetchProducts()
        setProductFeed(productList)
    }

    private fun fetchProducts(): List<Product> {
        val response = restTemplate.getForEntity(
            "${apiProtocol}://${apiHost}:${apiPort}${apiEndpoint}",
            Array<Product>::class.java
        )
        if (response.statusCode.is2xxSuccessful) {
            return response.body?.toList() ?: emptyList()
        } else {
            throw RuntimeException("Failed to fetch products from external API")
        }
    }

    fun initializeProducts(customProducts: () -> List<Product>) {
        setProductFeed(customProducts())
    }

    fun getProductById(id: Long): Product? {
        return productFeed.find { it.id == id }
    }

    fun getProductsByCategory(category: String, inStock: Boolean?): List<Product> {
        return productFeed.filter { it.category == category }
            .let { if (inStock == true) it.filter { it.stockLevel > 0 } else it }
    }

    fun updateProduct(id: Long, product: Product): Product? {
        return productFeed.find { it.id == id }
            ?.apply {
                name = product.name
                description = product.description
                category = product.category
                price = product.price
                stockLevel = product.stockLevel
            }
    }

    fun updateStockLevel(id: Long, stockLevel: Int): Product? {
        return productFeed.find { it.id == id }
            ?.apply { this.stockLevel = stockLevel }
    }
}