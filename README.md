# OOPS Project

## Team Members

1. **Momin M**  
   - Roll No: 23bds036  
   - **Algorithm**: Movie Recommendation using Bipartite Graph  
   - **Dataset**: Movielens
   - **Description**: This algorithm uses a Bipartite Graph for movie recommendations based on user preferences and ratings.
   - **Output**: Shown in GUI

2. **Preetham P**  
   - Roll No: 23bds046  
   - **Algorithm**: Knuth-Morris-Pratt Algorithm  
   - **Dataset**: Kaggle Mobile-Usage-Data
   - **Description**: This algorithm implements the Knuth-Morris-Pratt string matching technique for pattern matching in large text datasets.
   - **Output**: Shown in GUI

3. **Mohana Krishnan V**  
   - Roll No: 23bds035  
   - **Algorithm**: Maximum Flow (No. of vehicles from source to sink) in the graphical representation of Chicago Road Network  
   - **Dataset**: Source: GitHub. Directed graph of Chicago's road network where each edge has maximum flow capacity
   - **Description**: This algorithm calculates the maximum flow (number of vehicles) in a directed graph representing the road network of Chicago using a maximum flow algorithm.
   - **Output**: Output will be written into a file called as output.txt which will be created in the directory OOPSproject->FordFulkerson

## Directory Structure

The OOPS Project contains the following directories:

1. **FordFulkerson**
2. **KMP**
3. **MovieRecommendation**
inside each of those directories you will find a java file should be run in order to get the output.

## Setup Instructions

You can directly run the cloned repository on **VSCode**.

### Note:
Since the repository is made in such a way that it can be directly run on VSCode, if you want to run the code in **IntelliJ** or **Eclipse**, you will need to make a few changes.

### Steps for Eclipse/IntelliJ:

1. After cloning the repo and opening it in either **Eclipse** or **IntelliJ**, click on **Quick Fix** on the Java class with the name of the respective file for all three files.

    Example: If the name of the file is `FordFulkerson`, click on **Quick Fix** (if it shows) on the Java class named `public class FordFulkerson`.

2. Modify the following files as per the instructions:

    #### **FordFulkerson**

    - **Path**: `OOPSproject -> FordFulkerson -> FordFulkerson.java`
    - **Line No: 132**  
      **Actual**:  
      ```java
      String inputFileName = store + "\\src\\OOPSproject\\FordFulkerson\\Chicago.tntp";
      ```
      **Modify it to**:  
      ```java
      String inputFileName = store + "\\src\\OOPSproject\\FordFulkerson\\Chicago.tntp";
      ```

    - **Line No: 148**  
      **Actual**:  
      ```java
      String outputFileName = store + "\\src\\OOPSproject\\FordFulkerson\\output.txt";
      ```
      **Modify it to**:  
      ```java
      String outputFileName = store + "\\src\\OOPSproject\\FordFulkerson\\output.txt";
      ```

    #### **MovieRecommendation**

    - **Path**: `OOPSproject -> MovieRecommendation -> MovieRecommendationApp.java`
    - **Line No: 24**  
      **Actual**:  
      ```java
      initializeDataFromFile(store + "\\src\\OOPSproject\\MovieRecommendation\\javaproject.txt");
      ```
      **Modify it to**:  
      ```java
      initializeDataFromFile(store + "\\src\\OOPSproject\\MovieRecommendation\\javaproject.txt");
      ```

    - **Line No: 25**  
      **Actual**:  
      ```java
      initializeMovieNames(store + "\\src\\OOPSproject\\MovieRecommendation\\movies.csv");
      ```
      **Modify it to**:  
      ```java
      initializeMovieNames(store + "\\src\\OOPSproject\\MovieRecommendation\\movies.csv");
      ```

## Running the Project

Once you've made the necessary modifications, you can run the project in **Eclipse** or **IntelliJ** by following these steps:

1. Open the project in your IDE.
2. Navigate to the desired algorithm folder (FordFulkerson, KMP, or MovieRecommendation).
3. Run the Java class (`Main` or the entry-point class) in each algorithm folder to execute the respective algorithms.


