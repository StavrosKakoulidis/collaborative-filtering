package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {

    private final Properties properties;

    public AppConfig() {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getN(){
        return Integer.parseInt(properties.getProperty("n"));
    }

    public int getM(){
        return Integer.parseInt(properties.getProperty("m"));
    }

    public int getPercentage(){
        return Integer.parseInt(properties.getProperty("percentage"));
    }

    public String getSimilarityFunction(){
        return properties.getProperty("similarity_function");
    }

    public String getPredictionFunction(){
        return properties.getProperty("prediction_function");
    }

    public int getK(){
        return Integer.parseInt(properties.getProperty("k"));
    }
}
