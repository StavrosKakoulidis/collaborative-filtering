# Item-to-Item Collaborative Filtering Simulator

This Java program simulates the Item-to-Item Based Collaborative Filtering technique for recommendation systems. It generates a user-item matrix with random ratings and allows users to choose from various similarity functions (Jaccard, cosine, adjusted cosine, and Dice) and prediction methods (KNN with average or weighted average of ratings). The program then calculates predicted ratings for a randomly selected percentage of the matrix and computes the mean error as the output.

## Table of Contents

- [Introduction](#item-to-item-collaborative-filtering-simulator)
- [Features](#features)
- [How to Use](#how-to-use)
- [Configuration](#configuration)

## Features

- Generates a user-item matrix with random ratings.
- Supports multiple similarity functions (Jaccard, cosine, adjusted cosine, Dice).
- Provides two prediction methods: KNN with average and KNN with weighted average of ratings.
- Allows users to specify the percentage of known values for matrix ratings.
- Calculates mean error for predicted values.

## How to Use

1. Clone this repository to your local machine.
2. Customize the simulation by configuring the parameters such as matrix size, similarity function, prediction method, and percentage of known values in the config.properties file.
3. Run the program.

## Configuration
You can customize the simulation by editing the config.properties file:
- n: Set the dimensions of the user-item matrix (N x M)
- m: Set the dimensions of the user-item matrix (N x M)
- percentage: Set the percentage of known values in the matrix.
- similarity_function: Choose between jaccard, cosine, adjusted cosine, and dice for similarity calculation.
- prediction_function: Choose between KNN with average and KNN with weighted average for rating prediction.
- k: Set the number of parameter k for KNN.
