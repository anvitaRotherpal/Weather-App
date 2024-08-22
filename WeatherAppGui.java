

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.json.simple.JSONObject;

public class WeatherAppGui extends JFrame {

    private JSONObject weatherData;

    public WeatherAppGui() {
        // Set up GUI
        super("Weather App");

        // Configure GUI to end the program's process
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set size of GUI (in pixels)
        setSize(650, 450);

        // Load our GUI at the center of the screen
        setLocationRelativeTo(null);

        // Prevent resize of GUI
        setResizable(false);

        setLayout(null);

        addGuiComponents();
    }

    private void addGuiComponents() {
        // Search field
        JTextField searchTextField = new JTextField();

        // Set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        // Change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        // Weather image
        JLabel weatherConditionImage = new JLabel(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // Temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // Center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 400, 450, 36); // Adjusted bounds to fit the GUI
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // Humidity image
        JLabel humidityImage = new JLabel(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // Humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // Windspeed image
        JLabel windspeedImage = new JLabel(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\windspeed.png"));
        windspeedImage.setBounds(180, 500, 85, 55);
        add(windspeedImage);

        // Windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // Search button
        JButton searchButton = new JButton(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\search.png"));

        // Change the cursor to hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        add(searchButton);

        // Add action listener to search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get location from user
                String userInput = searchTextField.getText();

                // Validate input - remove whitespace to ensure non-empty text
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                // Retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // Update GUI if weather data is retrieved successfully
                if (weatherData != null) {
                    // Update weather image
                    String weatherCondition = (String) weatherData.get("weather_condition");

                    // Depending on the condition, update the weather condition image
                    switch (weatherCondition) {
                        case "Clear":
                            weatherConditionImage.setIcon(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\clear.png"));
                            break;
                        case "Cloudy":
                            weatherConditionImage.setIcon(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\cloudy.png"));
                            break;
                        case "Rain":
                            weatherConditionImage.setIcon(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\rain.png"));
                            break;
                        case "Snow":
                            weatherConditionImage.setIcon(loadImage("C:\\Users\\Anvita\\Desktop\\assets\\snow.png"));
                            break;
                    }

                    // Update temperature text
                    double temperature = (double) weatherData.get("temperature");
                    temperatureText.setText(temperature + " C");

                    // Update weather condition text
                    weatherConditionDesc.setText(weatherCondition);

                    // Update humidity text
                    long humidity = (long) weatherData.get("humidity");
                    humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                    // Update windspeed text
                    double windspeed = (double) weatherData.get("windspeed");
                    windspeedText.setText("<html><b>Windspeed</b> " + windspeed + " km/h</html>");
                } else {
                    System.out.println("Weather data could not be retrieved.");
                }
            }
        });
    }

    // Used to create images in our GUI components
    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // Return the image icon so that our component can render it
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not find resource: " + resourcePath);
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new WeatherAppGui().setVisible(true);
            }
        });
    }
}