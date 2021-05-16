# Multi-Objective Software Effort Estimation: A Replication Study

This repository is supplementary to the paper entitled "Multi-Objective Software Effort Estimation: A Replication Study",  which is currently under review at IEEE TSE. 
It contains the Java source code and datasets used in the study.

### How to Compile (Build)
__JCoGEE__ is a [Maven](https://maven.apache.org/) project (`pom.xml` is in `jcogee/`). 
If you have not changed the code and are interested in running the tool you can skip this step and go to [How to Run](#how-to-run) the project.
if you have changed the code and wish to compile it, you will need to install Maven on your system first.
Once you have installed Maven, you can use via terminal the command `mvn clean install` from the `jcogee/` directory to compile the project.
You may need an internet connection, if Maven needs to download some dependencies during the building process.

### How to Run
After compilation, you can find two .jar files in the `jcogee/target/` directory: `cogee-1.0.jar` and `cogee-1.0-jar-with-dependencies.jar`.
The .jar file with dependencies is executable directly from the terminal.
You need to pass three parameters to the jar file to execute: 

  1. The function which is either _`cogee`_ for running CoGEE, _`pred`_ for providing predictions for the projects in the test set, or _`eval`_ for producing the evaluation files. You can also specify a combination of two or all three at once. For example, `-func cogee,pred` or `-func cogee,pred,eval` (options separated by comma or dot, but not space!).
 
     - (!) Please note that if you want to use `eval` and/or `pred` functions without `cogee`, you need to run the tool with `cogee` option at least once before that. This is because `eval` and `pred` work with the solution files that `cogee` produces.

	 - The default option for this parameter is `cogee`. It means that if you do not pass the `-func` parameter, the tool will run `cogee` by default.
  
  2. The root path (`-path [Your Path]`). [__Your Path__] refers to the path in which you have placed the configuration file ([__cogee.cfg__](#the-configuration-file)) and the __dataset__ directory. The output files will also be saved in this path. 

  	 - The default option for this parameter is the current directory (i.e. '.'). It means that if you do not pass the `-path` parameter, the tool will look for the configuration file and the dataset directory in the current path, which is the path containing the executable .jar file.
  
  3. The dataset name (`-ds [Dataset Name]`). You need to specify a dataset name. For example, `-ds maxwell`.
  
To run the tool, for instance, you can use the following command, which will run CoGEE on Maxwell training dataset and will use its output models to predict the effort for the projects in the Maxwell test dataset:

	java -jar cogee-1.0-jar-with-dependencies.jar -func cogee,pred -path [Your Path] -ds maxwell

The CoGEE tool also needs a configuration file from which it reads the algorithm type, the configuration of the evolutionary algorithm, the objectives, and the number of times you want to repeat the run.

## The Configuration File

This file contains the configuration needed by GoGEE to run. The file has to be located in the root of the directory indicated by the `-path` parameter and named _cogee.cfg_ .
Each line of this file is a command used to run an experiment. Each experiment performs _n_ independent runs on all _k_ folds of a dataset using an algorithm with the specified configuration. 
Each line is composed by three parts separated by colons (i.e. ":") following the sintax below

`algorithm_name:objectives:GA_configuration`

### Algorithms

The tool offers five multi-objective evolutionary algorithms and one single objective evolutionary algorithm.
The following algorithm names can be used in as `_algorithm_name_` in the command `algorithm_name:objectives:GA_configuration`:

#### Multi-Objective Algorithms:

- __IBEA__: for Indicator-Based Evolutionary Algorithm (IBEA).

- __MOCell__: for Multi-objective Cellular Genetic Algorithm (MOCell).

- __NSGA2__: for Non-Dominated Sorting Genetic Algorithm II (NSGA-II).

- __NSGA3__: for Non-Dominated Sorting Genetic Algorithm III (NSGA-III).

- __SPEA2__: for Strength Pareto Evolutionary Algorithm 2 (SPEA2).

#### Single-Objective Algorithm:

- __GA__: for Generational Genetic Algorithm (standard GA).

### Objectives

The tool offers five different objective functions. The following objective names can be used in the configuration file as  `objectives ` in the command `algorithm_name:objectives:GA_configuration`:

- __SAE__: Sum of the Absolute Errors

- __MAE__: Mean Absolute Errors

- __CI__:  Confidence Interval of the Errors

- __SOE__: Sum of Overestimates

- __SUE__: Sum of Underestimates

You can use as many objectives as you want for each experiment by listing their names in the second part of the configuration line, separating them by commas (","). 

### The GA Configuration

The `GA_configuration ` part of the command `algorithm_name:objectives:GA_configuration` is composed of five parts separated by single dash (i.e., "-"). Each part indicates a configuration parameter and starts with an upper case character (G, P, C, M, and R) followed by a number indicating the parameter value:

`G0-P0-C0.0-M0.0-R0`

- __G__: the number of Generations. This number should be an integer value greater than 0.

- __P__: the size of the population. This number should be an integer value greater than 0.

- __C__: the Cross-Over Rate. This number should be a double value between 0.0 and 1.0.

- __M__: the Mutation Rate. This number should be a double value between 0.0 and 1.0.

- __R__: the number of independent runs we need to run. This number should be an integer value greater than 0.


### Sample Configuration Line

A sample configuration line can be:

`NSGA2:SAE,CI:G250-P100-C0.5-M0.1-R30`

This will run NSGA-II with two objectives, namely, the sum of absolute errors (SAE) and confidence interval (CI), while the population size will be 100 individuals, running for 250 generations with cross-over and mutation rates set to 0.5 and 0.1, respectively, and the experiment will execute 30 independent runs.

## Data set

The `datasets/` directory contains five datasets. If you want to run CoGEE with your own dataset, you need to prepare the files and put them inside this directory so that CoGEE can use it. We explain below how to prepare such files.

### Preparing the Dataset File

CoGEE is implemented to work with _k_-fold cross-validation. 
So it needs two files per dataset. 
One containing the training sets and the other containing the test sets. 
The name of the train and test files should end with _-train.xls_ and _-test.xls_, respectively.

Each of the train and test files are excel files containing _k_ sheets, one per fold. The sheet names should be unique (_fold[n]_ is recommended as sheet names, with _[n]_ being the fold numbe). Each fold is a table with at least three columns with headers: ID, Effort, and at least one feature column. _ID_ is a unique identification number for a project and _Effort_ is the actual effort for that project. CoGEE looks for these two columns in each fold table to map the dataset to a data structure which is used in evolution and evaluation phases. Every other column will be considered as a feature (independent variable) in the dataset.

There can be multiple feature columns between ID and Effort. ID should contain an integer value, while the Effort and feature values can be integer or double.  

## Output

All the output files will be created under the directory indicated by the `-path` parameter. The tool creates a directory named __output__ under this path and creates the output files in that directory.
Under the `output/` directory, the output files are organized in directories (one per experiment). The directory's name indicates the dataset, evolutionary algorithm, objective(s), the configuration of the evolutionary algorithm, and the number of runs. For instance, _"maxwell-NSGA2(SAE,CI)[G250-P100-C0.5-M0.1-R30]"_ contains the output files for CoGEE experimented on the Maxwell dataset with NSGA-II using two objectives (SAE, and CI), 250 generations, population of size 100, 0.5 cross-over rate and 0.1 mutation rate, 30 runs.

The output of the evolutionary effort estimation model building process contains two files per run. One contains the Pareto Front and the other contains the chromosome values. The name of the former ends in _-GA-PF.csv_ and the latter in _-GA-VAR.csv_.

If the `eval` option was passed, the tool will evaluate the test set using the non-dominated solutions (models) built in the evolutionary process and will produce two files per experiment (each experiment is running _n_ independent runs on all _k_ folds of a dataset using an algorithm with one configuration). 

The files contain the results of the evaluation per project for each run, reporting the Mean Absolute Error (MAE) and the Median Absolute Error (MdAE) of the prediction for each Project in each Run (name starting with [dataset name]-_EvaluationResult-PerProject_) and the results of the evaluation per run, reporting Mean/Median MAE, Mean/Median MdAE, and CI of the predictions made by models produced by each run on the test set (name starting with [dataset name]-_EvaluationResult-PerRun_).

If the `pred` option was passed, the tool will also produce a file per run containing the predicted effort for each project in the test set, using each of the non-dominated models built in the evolutionary process. The name of these files ends in _-Predictions.csv_.
Each record in the prediction table contains the Project ID, its Actual Effort value, and the predicted effort with each one of the chromosomes in the _-GA-VAR.csv_ file.
