package com.company;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {
    private static int N = 100; // number of users
    private static int M = 100; // number of items
    private static int PERCENTAGE = 80;
    private static String SIMILARITY_FUNCTION = "jaccard"; //{jaccard, cosine, dice, adjusted cosine}
    private static String PREDICTION_FUNCTION = "weighted"; //{weighted, average}
    private static int K = 10;

    public static void main(String[] args) {

        AppConfig config = new AppConfig();

        N = config.getN();
        M = config.getM();
        PERCENTAGE = config.getPercentage();
        SIMILARITY_FUNCTION = config.getSimilarityFunction();
        PREDICTION_FUNCTION = config.getPredictionFunction();
        K = config.getK();

        double[][] benefit_table = new double[N][M];
        double mean_error = 0;
        int num_of_iterations = 10;

        // Create the benefit table
        Random generator = new Random();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {

                double r = 1.0 + (10.0 - 1.0) * generator.nextDouble();
                benefit_table[i][j] = r;
            }
        }

        for(int i =0 ; i<num_of_iterations ; i++) {
            mean_error+= mean_error(benefit_table);
        }
        System.out.println("MEAN ERROR === " + mean_error/num_of_iterations);


    }


    private static double mean_error(double[][] benefit_table) {

        double[][] known_benefit_table = new double[N][M];
        double[][] predicted_benefit_table = new double[N][M];
        // This backup table is used to keep the initial values of the known benefit table
        // before they become adjusted from the adjusted cosine method so we can make the
        // prediction for the missing values.
        double[][] backup_benefit_table = new double[N][M];
        double mean_error = 0;

        Random generator = new Random();

        // Create the benefit table

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                known_benefit_table[i][j] = 0;
                backup_benefit_table[i][j] = 0;
            }
        }

        // create the known benefit table of a given percentage

        int known_items = PERCENTAGE*N*M/100;
        int count_items=0;

        while(count_items< known_items) {

            int row = generator.nextInt(N);
            int col = generator.nextInt(M);

            if(known_benefit_table[row][col] == 0) {

                known_benefit_table[row][col] = benefit_table[row][col];
                backup_benefit_table[row][col] = benefit_table[row][col];

                count_items++;
            }

        }

        if(SIMILARITY_FUNCTION.equals("adjusted cosine")) {
            adjustTable(known_benefit_table);
        }


        for(int i=0 ; i<N ; i++) {
            for(int j=0; j<M ; j++) {
                if(known_benefit_table[i][j]==0) {
                    predicted_benefit_table[i][j]= predictValue(known_benefit_table,i,j,backup_benefit_table);
                }
            }
        }

        int count = 0 ;

        for(int i=0 ; i<N ; i++) {
            for(int j=0; j<M ; j++) {
                if(predicted_benefit_table[i][j]!=0) {
                    mean_error +=Math.abs(predicted_benefit_table[i][j]-benefit_table[i][j]);
                    count++;
                }
            }
        }
        return mean_error/count;
    }


    private static double predictValue(double[][] known_benefit_table, int i, int j, double[][] backup_benefit_table) {

        double [] target_vec = new double[N];
        double [] current_vec = new double[N];
        ArrayList<Double> similarities_list = new ArrayList<>();
        double [][] knn;
        double predicted_value = 0;

        //populate target vector

        for(int row = 0 ; row<N ; row++) {


            target_vec[row]=known_benefit_table[row][j];

        }



        for(int col =0 ; col<M ; col++) {

            for(int row = 0 ; row<N ; row++) {

                current_vec[row]=known_benefit_table[row][col];

            }

            current_vec[i]=0;

            switch (SIMILARITY_FUNCTION) {
                case "cosine":
                case "adjusted cosine":

                    similarities_list.add(cosineSimilarity(target_vec, current_vec));

                    break;
                case "jaccard":

                    similarities_list.add(jaccardSimilarity(target_vec, current_vec));

                    break;
                case "dice":

                    similarities_list.add(diceSimilarity(target_vec, current_vec));
                    break;
            }

        }



        similarities_list.set(j, -100.0);

        knn = find_knn(similarities_list);

        if(PREDICTION_FUNCTION.equals("average")) {

            int count =0;

            for(int z =0 ; z<K ; z++) {

                if(backup_benefit_table[i][(int) knn[z][1]] != 0) {

                    predicted_value += backup_benefit_table[i][(int) knn[z][1]];
                    count++;
                }
            }
            if(count != 0) {
                return predicted_value/count;
            }else {
                return predicted_value;
            }

        }else if(PREDICTION_FUNCTION.equals("weighted")){

            double count = 0;

            for(int z =0 ; z<K ; z++) {

                if(backup_benefit_table[i][(int) knn[z][1]] != 0) {

                    predicted_value += backup_benefit_table[i][(int) knn[z][1]] * knn[z][0];
                    count += knn[z][0];
                }
            }

            if(count != 0) {
                return predicted_value/count;
            }else {
                return predicted_value;
            }
        }
        return 0;
    }


    private static double cosineSimilarity(double[] vectorA, double[] vectorB) {

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }


    private static Double diceSimilarity(double[] a, double[] b) {

        double intersection =0;
        double cardinality = 0;

        for(int i =0 ; i<N ; i++) {
            if(a[i]!=0) {
                if(b[i]!=0) {
                    intersection ++;
                }

                cardinality++;
            }
            if(b[i]!=0) {
                cardinality++;
            }
        }
        return 2*intersection/cardinality;
    }


    private static double[][] find_knn(ArrayList<Double> similarities_list) {

        double [][] knn = new double[K][2];

        for(int z =0 ; z<K ; z++) {

            int index = similarities_list.indexOf(Collections.max(similarities_list));

            knn[z][0] = Collections.max(similarities_list);
            knn[z][1] = index;
            similarities_list.set(index, -100.0);

        }

        return knn;
    }

    private static Double jaccardSimilarity(double[] a, double[] b) {

        double intersection = 0;
        double union = 0;


        for (int i = 0; i < N; i++) {

            if (a[i] != 0) {

                if (b[i] != 0) {
                    intersection++;
                }
                union++;
            } else {

                if (b[i] != 0) {
                    union++;
                }
            }
        }
        return intersection / union;
    }


    private static void adjustTable(double[][] table) {

        for(int i=0 ; i<N ; i++) {

            double sum =0;
            double count=0;

            for(int j=0; j<M ; j++) {

                if(table[i][j]!=0) {
                    sum+= table[i][j];
                    count++;
                }
            }

            for(int j=0; j<M ; j++) {

                if(table[i][j]!=0) {
                    table[i][j]= table[i][j]-sum/count;
                }
            }
        }
    }

}
