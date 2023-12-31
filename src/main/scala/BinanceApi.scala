import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.parse
import java.io.{BufferedReader, InputStreamReader}
import java.net.{HttpURLConnection, URL}
import scala.util.{Failure, Success, Try}

class BinanceAPI extends SourceFunction[Double] {
  private val apiKey = "tmgeck4COqGcuYrlapbT6I86ANJszX2FKPMiusnJbFaHkSsEW06XtuiUABzVLvka"
  private val symbol = "BTCUSDT"
  private val endpoint = s"https://api.binance.com/api/v3/ticker/price?symbol=$symbol"

  override def run(ctx: SourceFunction.SourceContext[Double]): Unit = {
    while (true) {
      Try {
        val url = new URL(endpoint)
        val connection = url.openConnection.asInstanceOf[HttpURLConnection]
        connection.setRequestMethod("GET")
        connection.setRequestProperty("X-MBX-APIKEY", apiKey)

        val responseCode = connection.getResponseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
          val in = new BufferedReader(new InputStreamReader(connection.getInputStream))
          val response = new StringBuilder
          var inputLine: String = null
          while ({ inputLine = in.readLine; inputLine != null }) {
            response.append(inputLine)
          }
          in.close()

          val jsonResponse = response.toString
          implicit val formats: DefaultFormats.type = DefaultFormats
          val json = parse(jsonResponse)
          val price = (json \ "price").extract[String].toDouble
          ctx.collect(price)
        } else {
          throw new RuntimeException(s"HTTP GET request failed with error code: $responseCode")
        }
      } match {
        case Success(_) =>
        case Failure(ex) =>
          ex.printStackTrace()
      }

      Thread.sleep(13000) // Stream the data every 13 seconds
    }
  }

  override def cancel(): Unit = {}
}
