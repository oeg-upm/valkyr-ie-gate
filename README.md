# Valkyr-IE-Gate

Valkyr-IE-Gate is a library for information extraction using the library GATE. The idea is to ease and encapuslate the complexity of the library to work with a simple API to exploit the two main components of the original library: Gazetteers and Jape Rules.

The idea of the library is to work as a microservice web API in which you configure only declare and configure the main processes and you receive the annotations of the document in a JSON document. Easy to integrate and use with other NLP libraries of the domain to be invoked by other languages as Python.  


## Requirements

Valkyr-IE needs of two local files:

1. config (folder). A folder which contains the local configuration of GATE. The folder will be deployed when the proyect is compiled.
2. processes.conf (file). File that declares the different processes that are performed over the document.

Both files are need to be in the path in which the jar is executed 

### Processes.conf
This is the configurable file in which you declare the processes to be executed by GATE. For now, there are 5 processes that can be declared:
1. Tokenizer. Standard tokenizer of ANNIE
2. SentenceSpiltter. ANNIE process to identify sentences
3. Gazetteer_ns. Gazetteer No Case Sensitive. The path to the file .def must be declared
4. Gazetteer_s. Gazetteer Sensitive. The path to the file .def must be declared
5. Jape. Jape Rule. The path to the file .jape must be declared




## Install and Compile

For install and compile just:
```
mvn clean install
```

The folder of config will be deployed in the same directory

## Execute

In a folder in which the config folder and processes.conf exits:
```
java -jar target/valkyr-ie-gate-1.0.jar      
```

Morevover, you can declare multiple instances in different ports
```
java -jar -Dserver.port=8084 target/valkyr-ie-gate-1.0.jar      
```

