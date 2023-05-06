package nl.streaem.assessment.controller

import nl.streaem.assessment.model.Product
import nl.streaem.assessment.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: Long): ResponseEntity<Product> {
        val product = productService.getProductById(productId)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getProductsByCategory(
        @RequestParam(required = true) category: String,
        @RequestParam(required = false, defaultValue = "false") inStockOnly: Boolean
    ): List<Product> {
        return productService.getProductsByCategory(category, inStockOnly)
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody updatedProduct: Product
    ): ResponseEntity<Product> {
        val product = productService.updateProduct(productId, updatedProduct)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{productId}/stock")
    fun updateStockLevel(
        @PathVariable productId: Long,
        @RequestParam(required = true) newStockLevel: Int
    ): ResponseEntity<Product> {
        val product = productService.updateStockLevel(productId, newStockLevel)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}