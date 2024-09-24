import kotlinx.serialization.json.Json
import model.BenchmarkSettings
import java.nio.file.Paths

object ConfigUtils {
    fun readConfig(): String {
        val configPath = Paths.get( "..","config", "benchmark_setting.json").toFile()

        if (!configPath.exists()) {
            throw IllegalStateException("Config file not found: ${configPath.absolutePath}")
        }

        return configPath.readText()
    }

    fun parseConfig(jsonContent: String): BenchmarkSettings {
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(jsonContent)
    }
}
