package pt.isel
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebFluxApplication

fun main(args: Array<String>) {
    var port = System.getenv("PORT")
    if (port == null) {
        port = "7070"
    }

    System.setProperty("server.port", port)

    println("Running on port: $port")
    runApplication<WebFluxApplication>(*args)
}
