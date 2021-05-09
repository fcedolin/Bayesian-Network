/**
 * class Bayesiannet.java
 *
 * @author Federico Cedolini
 * @version 1.0
 * @since 05/09/2021
 * Known issues: none
 */

import java.util.*;
import java.io.*;
import java.lang.*;

public class Bayesiannet {
    //Probability tables
    static double Course[] = {0.5, 0.5};
    static double Weather[] = {0.3, 0.2, 0.5};
    static double HarePerf[][] = {{0.5, 0.3, 0.2}, {0.1, 0.2, 0.7}, {0.0, 0.2, 0.8}, {0.7, 0.2, 0.1}, {0.2, 0.4, 0.4}, {0.1, 0.3, 0.6}};
    static double TortoisePerf[][] = {{0.2, 0.3, 0.5}, {0.4, 0.5, 0.1}, {0.3, 0.5, 0.2}, {0.2, 0.4, 0.4}, {0.2, 0.5, 0.3}, {0.4, 0.4, 0.2}};
    static double HareWins[] = {0.5, 0.1, 0.0, 0.8, 0.5, 0.2, 0.9, 0.7, 0.5};

    public static void main(String[] args) {
        int iterations, option;
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of iterations: ");
        iterations = sc.nextInt();
        sc.skip(".*");

        do{
            System.out.println("\n\nEnter an integer to interact with the menu");
            System.out.println("1) In general, how likely is the Hare to win?");
            System.out.println("2) Given that is it coldWet, how likely is the Hare to win?");
            System.out.println("3) Given that the Tortoise won on the short course, what is the probability distribution for the Weather?");
            System.out.println("4) Change number of iterations.");
            System.out.println("5) Exit");
            System.out.print("Your option: ");
            option = sc.nextInt();
            sc.skip(".*");

            switch (option){
                case 1:
                    question1(iterations);
                    break;
                case 2:
                    question2(iterations);
                    break;
                case 3:
                    question3(iterations);
                    break;
                case 4:
                    System.out.print("Enter the number of iterations: ");
                    iterations = sc.nextInt();
                    sc.skip(".*");
                    break;
                case 5:
                    System.out.println("BYE!");
                    break;
                default:
                    System.out.println("INVALID SELECTION");
                    break;
            }

        }while(option != 5);

    }//main

    /**
     * question1 calculates the probability of the Hare winning
     * @param iterations is the number of samples to generate
     */
    public static void question1(int iterations){
        double counter = 0.0;
        int courseIndex, weatherIndex, HarePerfIndex, TortoisePerfIndex;
        for(int i = 0; i < iterations; i++){
            courseIndex = selection1d(Course);
            weatherIndex = selection1d(Weather);
            HarePerfIndex = selection2d(HarePerf, courseIndex, weatherIndex, Weather.length);
            TortoisePerfIndex = selection2d(TortoisePerf, courseIndex, weatherIndex, Weather.length);
            if(finalSelection(HareWins, HarePerfIndex, TortoisePerfIndex, HarePerf[0].length))
                counter++;
        }
        System.out.println("\nAfter " + iterations + " the probability of the Hare to win is " + counter/iterations);

    }//question1

    /**
     * question2 calculates the probability of Hare to win given coldwet weather
     * @param iterations is the number of samples to generate
     */
    public static void question2(int iterations){
        double counter = 0.0;
        double winCounter = 0.0;
        int ColdWetIndex = 0;
        int courseIndex, weatherIndex, HarePerfIndex, TortoisePerfIndex;

        for(int i = 0; i < iterations; i++){
            courseIndex = selection1d(Course);
            weatherIndex = selection1d(Weather);
            if(weatherIndex == ColdWetIndex) {
                counter++;
                HarePerfIndex = selection2d(HarePerf, courseIndex, weatherIndex, Weather.length);
                TortoisePerfIndex = selection2d(TortoisePerf, courseIndex, weatherIndex, Weather.length);
                if (finalSelection(HareWins, HarePerfIndex, TortoisePerfIndex, HarePerf[0].length))
                    winCounter++;
            }
        }
        System.out.println("\nAfter " + iterations + " the probability of the Hare to win given coldwet weather is " + winCounter/counter);
    }//question2

    /**
     * question3 calculates the probability distribution of the weather given that the Tortoise won in the short course
     * @param iterations is the number of samples to generate
     */
    public static void question3(int iterations){
        double winCounter = 0.0;
        double weatherCounter[] = {0.0, 0.0, 0.0};
        int courseIndex, weatherIndex, HarePerfIndex, TortoisePerfIndex;
        int shortCourseIndex = 0;

        for(int i = 0; i < iterations; i++){
            courseIndex = selection1d(Course);
            if(courseIndex == shortCourseIndex) {
                weatherIndex = selection1d(Weather);
                HarePerfIndex = selection2d(HarePerf, courseIndex, weatherIndex, Weather.length);
                TortoisePerfIndex = selection2d(TortoisePerf, courseIndex, weatherIndex, Weather.length);
                if (!finalSelection(HareWins, HarePerfIndex, TortoisePerfIndex, HarePerf[0].length)) {
                    winCounter++;
                    weatherCounter[weatherIndex]++;
                }
            }
        }
        System.out.println("Given the Tortoise won on the short course, the probability distribution for the Weather is as follow.");
        System.out.println("Coldwet: " + weatherCounter[0]/winCounter);
        System.out.println("Hot: " + weatherCounter[1]/winCounter);
        System.out.println("Nice: " + weatherCounter[2]/winCounter);
    }//question3


    /**
     * selection1d uses the probability of the input table to return the index for the next table
     * This is use for 1d arrays without any external interference, weather and course
     * @param table 1d table that contains the probabilities
     * @return index of specific weather or course
     */
    public static int selection1d(double table[]){
        double prob = Math.random();
        double sum = 0;
        for(int i = 0; i < table.length; i++){
            sum += table[i];
            if(prob <= sum){
                return i; //index
            }
        }
        System.out.println("ERROR in selection1d");
        return -1;
    }//selection1d

    /**
     *  selection1d uses the probability of the input table to return the index for the next table
     *  This is use for 2d tables with external influences, HarePerf and TortoisePerf
     * @param table 2d table that contains the probabilities
     * @param firstIndex index of the first variable that influences the probability
     * @param secondIndex index of the second variable that influences the probability
     * @param indexMultiplier given that the table is in order, indexMultiplier fixes index reference
     * @return index for next table
     */
    public static int selection2d(double table[][], int firstIndex, int secondIndex, int indexMultiplier){
        double prob = Math.random();
        int index = (firstIndex * indexMultiplier) + secondIndex;
        double sum = 0;
        for(int i = 0; i < table[index].length; i++){
            sum += table[index][i];
            if(prob <= sum){
                return i; //index
            }
        }
        System.out.println("ERROR in selection2d");
        return -1;
    }//selection2d

    /**
     *
     * @param table 1d table with final probabilities
     * @param firstIndex index of the first variable that influences the probability
     * @param secondIndex index of the second variable that influences the probability
     * @param indexMultiplier given that the table is in order, indexMultiplier fixes index reference
     * @return true if Hare wins, false otherwise
     */
    public static boolean finalSelection(double table[], int firstIndex, int secondIndex, int indexMultiplier){
        double prob = Math.random();
        int index = (firstIndex * indexMultiplier) + secondIndex;
        if(prob <= table[index]) {
            return true;
        }
        return false;
    }//finalSelection
}//Bayesiannet