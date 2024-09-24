package pt.isel
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MVCApplication

fun main(args: Array<String>) {
    var port = System.getenv("PORT")
    if (port == null) {
        port = "8080"
    }

    System.setProperty("server.port", port)

    println("Running on port: $port")
    runApplication<MVCApplication>(*args)
}
