import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object ExchangeRate {
  def main(args: Array[String]): Unit = {

    // Set up the streaming execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // Set up the BinanceAPI source
    val btcUsdExchangeRate = env.addSource(new BinanceAPI())

    // Define a fixed-size sliding window of 10 seconds with a slide interval of 5 seconds
    val windowSize = Time.seconds(10)
    val slideInterval = Time.seconds(5)

    // Apply a sliding window to the exchange rate stream
    val exchangeRateWindowed = btcUsdExchangeRate
      // Filter to keep only values greater than 1000
      .filter(rate => rate > 1000)
      .timeWindowAll(windowSize, slideInterval)
      .reduce((rate1, rate2) => rate1 + rate2)

    // Print the aggregated windowed data to the console
    exchangeRateWindowed.print()

    // Execute the streaming job
    env.execute("BTC/USD Exchange Rate Streaming with Sliding Windows")
  }
}
