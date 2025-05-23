<p align="center">
  <img src="https://raw.githubusercontent.com/alessio-vivaldelli/FileManager/master/src/main/resources/icona_2.png" width="20%" alt="File Manager">
</p>
<p align="center">
    <h1 align="center">File Manager</h1>
<p align="center">
    <em><code>A modern looks cross-platform file manager made in Java</code></em>
</p>
<br>

# FileManager
Cross-platform file manager written in Java

## Overview
This project is a Java-based application that provides a graphical user interface for viewing and managing files (file manager). The application uses Swing for the GUI and integrates with the FlatLaf look and feel for a modern appearance.
![FileManager](Screenshot/new-theme.png)

## Features
- File/Directory tags
- Add files/dir to favourite
- Drag and drop
  - Move multiple files/dirs in another directory
  - Add directory to shortcut 
  - drag and drop selection
- Glob pattern searching
- Multi tabs
- Clouds shortcut

## TODOs
- [ ] Contextual popup menu
- [ ] Improving shortcut
- [ ] Shortcut buttons for actions:
  - open terminal
  - vscode
  - ...
- [ ] Improving speed and background tasks
- [ ] Create tests

## Technologies Used
- Java 20
- Swing
- FlatLaf
- SQLite
- Maven

### Prerequisites
- Java 20 or higher
- Maven
- SQLite drivers

### Installation from source
1. Clone the repository:
    ```sh
    git clone https://github.com/alessio-vivaldelli/FileManager
    ```
2. Navigate to the project directory:
    ```sh
    cd FileManager
    ```
3. Build the project using Maven:
    ```sh
    mvn -B package --file pom.xml
    ```

#### Running the Application
1. Navigate to the `target` directory:
    ```sh
    cd target
    ```
2. Run the application:
    ```sh
    java -jar FileManager-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```

## Running the Application from Release jar
1. Navigate to the jar directory:
    ```sh
    cd <path-to-jar>
    ```
2. Run the application:
    ```sh
    java -jar FileManager-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```
## Acknowledgements
- [FlatLaf](https://www.formdev.com/flatlaf/)
- [MigLayout](http://www.miglayout.com/)
