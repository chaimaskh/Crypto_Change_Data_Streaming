import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.scene.chart.CategoryAxis; // Import CategoryAxis

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeDataVisualization extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Visualisation des données en temps réel");

        // Create Category and Number axes for the chart
        CategoryAxis xAxis = new CategoryAxis(); // Use CategoryAxis for the X-axis
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date and Time");
        yAxis.setLabel("Exchange Rate BTC|USD");

// Create the chart using the axes
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis); // Use LineChart<String, Number>
        lineChart.setTitle("Real-time Data BTC|USD");
        lineChart.setCreateSymbols(false); // Disable default symbols

// Create a data series for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>(); // Use XYChart.Series<String, Number>
        series.setName("Exchange Rate BTC|USD");


        // Add the series to the chart
        lineChart.getData().add(series);

        // Set up the chart appearance and features
        String seriesStyle = "-fx-stroke: #007ACC;"; // Blue color for the series
        series.getNode().setStyle(seriesStyle);

        // Show the main window
        Scene scene = new Scene(lineChart, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Use Exchange Rate data
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            double newValue = getRealTimeDataFromBinance(); // Replace this with actual data retrieval
            long timestamp = System.currentTimeMillis();

            // Convert timestamp to a readable date format
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));

            // Add data to the chart
            Platform.runLater(() -> {
                series.getData().add(new XYChart.Data<>(formattedDate, newValue));

                if (series.getData().size() > 100) {
                    series.getData().remove(0);
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    private double getRealTimeDataFromBinance() {
        // Simulated logic to retrieve real-time data from Binance
        return Math.random() * 42971.70;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
