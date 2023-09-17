# distributed-tracing-graph
Solved a directed graph problem represented by distributed tracing.

## Requirements
- Java 17
- Maven 3.8.2
- Docker (optional - if you want to run application using docker)

## Running distributed-tracing locally
distributed-tracing-graph is a application built using Maven. You can build a jar file and run it from the command line:


```
git clone https://github.com/ikoushikghosh/distributed-tracing-graph.git

cd distributed-tracing-graph

create input.txt (create a text file for input)
- touch input.txt for Linux or Mac
- echo.>input.txt for Windows

```
2) Open input.txt and write input:
```
AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7
```
3) Package and run the application:
```
mvn clean package

java -jar target/distributed-tracing-test.jar
```

## Running distributed-tracing with Docker
1) Set up on the command line
```
git clone https://github.com/ikoushikghosh/distributed-tracing-graph.git

cd distributed-tracing-graph

create input.txt (create a text file for input)
- touch input.txt for Linux or Mac
- echo.>input.txt for Windows

`````
2) Open input.txt and write input:
```
AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7
```

2) Build Docker Image
```
docker build -t distributed-tracing-test
```
3) Run Docker Image (interactive mode)
```
docker run -i distributed-tracing-test
```
4) Provide the input file path exiting into container
```
/koushik/distributed-tracing-test/input.txt
```


## Running with distributed-tracing in your IDE and review code

### Prerequisites
The following items should be installed in the system:
* Java 17.
* git command line tool
* Your preferred IDE (Any one)
    * Eclipse with the m2e plugin
    * IntelliJ IDEA

### Steps:

1) On the command line
```
git clone https://github.com/ikoushikghosh/distributed-tracing-graph.git

cd distributed-tracing-graph

create input.txt (create a text file for input)
- touch input.txt for Linux or Mac
- echo.>input.txt for Windows

```
2) Open input.txt and write input:
```
AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7
```

3) Inside Eclipse
```
File -> Import -> Maven -> Existing Maven project
```

Then build on the command line `mvn clean install`. Run the application main method by right clicking on it and choosing `Run As -> Java Application`.

4) Inside IntelliJ IDEA

In the main menu, choose `File -> Open` and select the `pom.xml`. Click on the `Open` button.

Run the application by right clicking on the `App` main class and choosing
`Run 'App'`.



