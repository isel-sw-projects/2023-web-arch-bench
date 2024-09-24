package pt.isel

import us.abstracta.jmeter.javadsl.JmeterDsl.*
import us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

fun main() {
    val jsonContent = ConfigUtils.readConfig()
    val config = ConfigUtils.parseConfig(jsonContent)

    var port = System.getenv("PORT")
    if (port == null) {
        port = "8080"
    }

    val url = "http://localhost:$port/api/"

    val currentDate = SimpleDateFormat("ddMMyyyy").format(Date())
    val name = config.benchmark.resultPath+"//$currentDate"

    var threadGroup = getThreadGroup(url)

    // Loop through the threadCountArray and add each rampToAndHold to the threadGroup
    for (threadCount in config.benchmark.simultaneousRequests!!) {
        threadGroup = threadGroup.rampToAndHold(
            threadCount,
            Duration.ofSeconds(config.benchmark.rampUpSeconds),
            Duration.ofSeconds(config.benchmark.durationSeconds)
        )
    }

    // Build the test plan
    val testPlan = testPlan(
        threadGroup,
        dashboardVisualizer(),
        htmlReporter(name).timeGraphsGranularity(Duration.ofSeconds(5))
    )

    // Get the current time
    val now = LocalDateTime.now()

    // Calculate the start of the next minute
    val nextMinute = now.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES)

    // Calculate the delay until the next minute
    val delay = ChronoUnit.MILLIS.between(now, nextMinute)

    // Schedule the test to start at the next minute
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    scheduler.schedule({
        try {
            testPlan.run()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            scheduler.shutdown()
        }
    }, delay, TimeUnit.MILLISECONDS)

    println("Test scheduled to start at: $nextMinute")

}



