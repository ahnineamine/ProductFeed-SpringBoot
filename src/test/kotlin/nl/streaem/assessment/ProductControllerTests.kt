package nl.streaem.assessment

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import nl.streaem.assessment.model.Product
import nl.streaem.assessment.service.ProductService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var productService: ProductService

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        productService.initializeProducts {
            listOf(
                Product(1L, "Product 1", "Description 1", 10.0, "Category A", 5),
                Product(2L, "Product 2", "Description 2", 20.0, "Category B", 0),
                Product(3L, "Product 3", "Description 3", 30.0, "Category A", 3),
                Product(4L, "Product 4", "Description 4", 40.0, "Category C", 0)
            )
        }
    }

    @Test
    fun `getProduct returns correct product`() {
        val productId = 1L
        val response = restTemplate.getForEntity("/products/$productId", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val product = objectMapper.readValue(response.body, Product::class.java)
        assertThat(product.id).isEqualTo(productId)
        assertThat(product.name).isEqualTo("Product 1")
        assertThat(product.description).isEqualTo("Description 1")
        assertThat(product.category).isEqualTo("Category A")
        assertThat(product.price).isEqualTo(10.0)
        assertThat(product.stockLevel).isEqualTo(5)
    }

    @Test
    fun `getProduct returns 404 for non-existent product`() {
        val productId = 99L
        val response = restTemplate.getForEntity("/products/$productId", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `getProductsByCategory returns correct products`() {
        val category = "Category A"
        val response = restTemplate.getForEntity("/products?category=$category", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val products = objectMapper.readValue(response.body, object : TypeReference<List<Product>>() {})
        assertThat(products).hasSize(2)
        assertThat(products[0].id).isEqualTo(1L)
        assertThat(products[1].id).isEqualTo(3L)
    }

    @Test
    fun `getProductsByCategory returns only in-stock products`() {
        val category = "Category A"
        val response = restTemplate.getForEntity("/products?category=$category&inStockOnly=true", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val products = objectMapper.readValue(response.body, object : TypeReference<List<Product>>() {})
        assertThat(products).hasSize(2)
        assertThat(products[0].id).isEqualTo(1L)
        assertThat(products[1].id).isEqualTo(3L)
    }

    @Test
    fun `getProductsByCategory returns empty list for non-existent category`() {
        val category = "Category D"
        val response = restTemplate.getForEntity("/products?category=$category", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val products = objectMapper.readValue(response.body, object : TypeReference<List<Product>>() {})
        assertThat(products).isEmpty()
    }

    @Test
    fun `updateProduct updates correct product`() {
        val productId = 2L
        val productToUpdate = Product(productId, "Updated Product", "Updated Description", 25.0, "Category B", 10)

        val responseBeforeUpdate = restTemplate.getForEntity("/products/$productId", String::class.java)
        assertThat(responseBeforeUpdate.statusCode).isEqualTo(HttpStatus.OK)
        val productBeforeUpdate = objectMapper.readValue(responseBeforeUpdate.body, Product::class.java)
        assertThat(productBeforeUpdate.name).isNotEqualTo(productToUpdate.name)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(productToUpdate, headers)
        val response = restTemplate.exchange("/products/$productId", HttpMethod.PUT, request, String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val responseAfterUpdate = restTemplate.getForEntity("/products/$productId", String::class.java)
        assertThat(responseAfterUpdate.statusCode).isEqualTo(HttpStatus.OK)
        val productAfterUpdate = objectMapper.readValue(responseAfterUpdate.body, Product::class.java)
        assertThat(productAfterUpdate.name).isEqualTo(productToUpdate.name)
    }
}