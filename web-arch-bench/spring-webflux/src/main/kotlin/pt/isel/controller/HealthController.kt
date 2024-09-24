package pt.isel.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ApiService

@RestController
@RequestMapping("/api")
class HealthController(private val apiService: ApiService) {

    @GetMapping("/ping")
    fun ping(): String {
        return apiService.async.ping()

    }

}