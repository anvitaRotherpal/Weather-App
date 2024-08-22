
import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Display our weather app GUI
                new WeatherAppGui().setVisible(true);

                // Example usage of WeatherApp methods
                System.out.println(WeatherApp.getLocationData("Tokyo"));
                System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}