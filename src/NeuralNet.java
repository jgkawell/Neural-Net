//A neural net that learns to return specific output images for specific 
//input images

import java.util.ArrayList;

public class NeuralNet {

    static KeyboardInputClass keyboard = new KeyboardInputClass();

    
    
    //holds the length of the sides for the square image arrays
    static int lengthOfImages;
    //holds the total number of images to learn
    static int numberOfImages;

    //these ArrayLists hold the int[][] that hold the 1 or -1 values of the images
    static ArrayList inputImages;
    static ArrayList targetImages;

    static int numberOfTrainingRuns = 0;
    static double learningRate = 0;

    static Neuron[][] inputNeuronArray;
    static Neuron[][] outputNeuronArray;

    static double[][][][] weightArray;

    //main method
    public static void main(String[] args) {
        
        readInTextFiles();

        printOriginalImages();

        promptUserForValues();

        inputNeuronArray = createNewNeuronArray();
        outputNeuronArray = createNewNeuronArray();
        weightArray = createNewWeightArray();

        trainingRunner();
    }

    //method with two loops for the multiple training runs and the different images
    private static void trainingRunner() {
        for (int currentTrainingRun = 0; currentTrainingRun < numberOfTrainingRuns; currentTrainingRun++) {
            System.out.println("\n\n=================================\nNEW TRAINING RUN...");
            System.out.print("Current training run: " + (currentTrainingRun + 1) + "\n");
            
            delayProgram(1500);
            
            for (int currentImage = 0; currentImage < (numberOfImages / 2); currentImage++) {
                trainingNeuralNet(currentImage);
                System.out.println("");
                printImages(currentImage);
                delayProgram(1500);
            }
        }
    }

    //master method to train the neural net
    private static void trainingNeuralNet(int currentImage) {
        for (int i = 0; i < lengthOfImages; i++) {
            for (int j = 0; j < lengthOfImages; j++) {
                //initialize the current activation at 0; add to it below
                double currentActivation = 0;

                currentActivation = calculateCurrentActivation(currentImage, currentActivation, i, j);

                //assign the calculated activation to the current output neuron
                outputNeuronArray[i][j].activation = currentActivation;

                //pull in the target value
                int target = ((int[][]) targetImages.get(currentImage))[i][j];

                //calculate the delta from the target to the activation
                outputNeuronArray[i][j].delta = target - currentActivation;

                calculateNewWeights(currentImage, i, j);
            }
        }
    }

    //calculates the current output neuron's activation value
    private static double calculateCurrentActivation(int currentImage, double currentActivation, int i, int j) {
        for (int k = 0; k < lengthOfImages; k++) {
            for (int l = 0; l < lengthOfImages; l++) {
                //fill in the activations of the input neurons
                inputNeuronArray[k][l].activation = ((int[][]) inputImages.get(currentImage))[k][l];

                //add in the new activation value
                currentActivation += inputNeuronArray[k][l].activation * weightArray[k][l][i][j];
            }
        }
        return currentActivation;
    }

    //calculates the new weights for the current output neuron
    private static void calculateNewWeights(int currentImage, int i, int j) {
        for (int k = 0; k < lengthOfImages; k++) {
            for (int l = 0; l < lengthOfImages; l++) {
                weightArray[k][l][i][j] = weightArray[k][l][i][j]
                        + learningRate * outputNeuronArray[i][j].delta
                        * ((int[][]) inputImages.get(currentImage))[k][l];
            }
        }
    }

    //prints the three images (input, target, and output)
    private static void printImages(int currentImage) {
        //loop to print all images
        for (int i = 0; i < lengthOfImages; i++) {
            //loop to print each row of the input
            for (int j = 0; j < lengthOfImages; j++) {
                checkAndPrint(((int[][]) inputImages.get(currentImage))[i][j]);
            }
            System.out.print("   ");
            //loop to print each row of the target
            for (int j = 0; j < lengthOfImages; j++) {
                checkAndPrint(((int[][]) targetImages.get(currentImage))[i][j]);
            }
            System.out.print("   ");
            //loop to print each row of the output neurons
            for (int j = 0; j < lengthOfImages; j++) {
                checkAndPrint(outputNeuronArray[i][j].activation);
            }
            System.out.println("");
        }
    }

    //checks the activation values and prints the correct chars
    private static void checkAndPrint(double value) {
        double toPrint = value;
        if (toPrint < 0) {
            System.out.print(" ");
        } else {
            System.out.print("X");
        }
    }

    //prompts and stores the user values for the neural net
    private static void promptUserForValues() {
        numberOfTrainingRuns = keyboard.getInteger(true, 20, 1, 1000000,
                "Enter the number of training repititions (default = 20):");

        learningRate = keyboard.getDouble(true, 0.01, 0, 1,
                "Enter the learning rate (default = 0.01):");
    }

    //populates a neuron array with blank neurons
    private static Neuron[][] createNewNeuronArray() {
        Neuron[][] array = new Neuron[lengthOfImages][lengthOfImages];
        for (int i = 0; i < lengthOfImages; i++) {
            for (int j = 0; j < lengthOfImages; j++) {
                array[i][j] = new Neuron();
            }
        }
        return array;
    }

    //populates a weight array with random weights
    private static double[][][][] createNewWeightArray() {
        //initialize new 4D array
        double[][][][] array = new double[lengthOfImages][lengthOfImages][lengthOfImages][lengthOfImages];
        //loops to populate 4D array with random values
        for (int i = 0; i < lengthOfImages; i++) {
            for (int j = 0; j < lengthOfImages; j++) {
                for (int k = 0; k < lengthOfImages; k++) {
                    for (int l = 0; l < lengthOfImages; l++) {
                        array[i][j][k][l] = Math.random() - 0.5;
                    }
                }
            }
        }
        return array;
    }

    //reads in the images from provided text files
    private static void readInTextFiles() {
        //create a TextFileClass object to read the files
        TextFileClass textFileObject = new TextFileClass();

        //read in the master file that has the list of other files
        textFileObject.getFileName("What is the name of the file to read?", "-1");
        textFileObject.getFileContents();
        String[] textFilesString = textFileObject.text;
        numberOfImages = 2 * Integer.parseInt(textFilesString[0]);

        //ask for the desired number of columns for each file/image
        lengthOfImages = keyboard.getInteger(true, 11, 1, 100,
                "Enter the side length of the files/images (default = 11):");

        //initialize 1D ArrayLists holding all in and out files
        inputImages = new ArrayList(numberOfImages / 2);
        targetImages = new ArrayList(numberOfImages / 2);

        //create an ArrayList that will hold all of the files as String[]
        ArrayList listOfTextFilesAsStringArrays = new ArrayList(numberOfImages);

        //for loop to run through and save all the files in the ArrayList
        for (int i = 1; i <= numberOfImages; i++) {
            //read in each file to save to the ArrayList
            TextFileClass textFileObjectLoop = new TextFileClass();
            textFileObjectLoop.getFileName("", textFilesString[i]);
            textFileObjectLoop.getFileContents();

            //add the file as a String[] to the ArrayList
            listOfTextFilesAsStringArrays.add(i - 1, textFileObjectLoop.text);
        }

        //for loop to copy over the individual files as 2D int arrays
        for (int i = 0; i < numberOfImages; i++) {
            String[] fileToPrint = (String[]) listOfTextFilesAsStringArrays.get(i);

            int rowCounter = 0;
            int columnCounter = 0;

            int[][] currentFileArray = new int[lengthOfImages][lengthOfImages];

            for (int j = 0; j < fileToPrint[0].length();) {
                //initiate the string to print
                String stringToPrint = "";

                //grab the char at j (it will be 1 or -)
                stringToPrint += fileToPrint[0].charAt(j);

                //if the first char is '-' then grab the next char (which will be 1)
                if (fileToPrint[0].charAt(j) == '-') {
                    stringToPrint += fileToPrint[0].charAt(j + 1);
                    j++;
                }

                //move on 2 positions to skip the space between numbers
                j += 2;

                //save the current integer in the currentFileArray
                currentFileArray[rowCounter][columnCounter] = Integer.parseInt(stringToPrint);

                //increase the counter to keep track of line breaks
                columnCounter++;

                //if a full row has been printed, line break
                if (columnCounter == lengthOfImages) {
                    rowCounter++;
                    columnCounter = 0;
                }
            }//end loop for copying each char to 2D int[][]

            //if i is even, save the currentArray to the in files ArrayList
            if (i % 2 == 0) {
                inputImages.add(currentFileArray);
            } else {
                targetImages.add(currentFileArray);
            }
        }//end loop for copying each file to 2D int[][]
    }

    //method to print the input and target images
    private static void print2DIntArrayAsXandO(int[][] arrayToPrint) {
        for (int i = 0; i < arrayToPrint.length; i++) {
            for (int j = 0; j < arrayToPrint[0].length; j++) {
                if (arrayToPrint[i][j] == 1) {
                    System.out.print("X");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
        System.out.println("");
    }

    //method to print the original images if requested by the user
    private static void printOriginalImages() {
        if (keyboard.getCharacter(true, 'N', "YN", 1, "Would you like to show "
                + "the input and output images? (Y/N):") == 'Y') {

            System.out.println("\nPrinting the target image(s):\n");
            delayProgram(750);
            for (int i = 0; i < inputImages.size(); i++) {
                print2DIntArrayAsXandO((int[][]) inputImages.get(i));
                
                delayProgram(250);
            }

            delayProgram(1500);
            System.out.println("");

            System.out.println("Printing the output image(s):\n");
            delayProgram(750);
            for (int i = 0; i < targetImages.size(); i++) {
                print2DIntArrayAsXandO((int[][]) targetImages.get(i));
                
                delayProgram(250);
            }
            delayProgram(1500);
        }
    }

    //method that delays the program for a specific amount of time
    private static void delayProgram(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException ex) {
            System.out.println("Something broke in delayProgram...");
        }
    }
}
