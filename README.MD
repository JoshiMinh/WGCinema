# University Secondary Term Software Project

## Running the Application

To compile the Java files and execute the program, follow these steps:

1. Ensure you have all the necessary Java files in the directory.
2. Open your command line interface.
3. Run the following commands:

    ```sh
    javac *.java
    ```

4. Set up the database connection with the following parameters:

    ```java
    String url = "jdbc:sqlserver://JoshiNitro5\\MSSQLSERVER02:1433;database=CinemaData";
    String username = "AdminCinema";
    String password = "Admin";
    ```

5. Run the main program:

    ```sh
    java -cp ".;sqljdbc42.jar" Booking
    ```

## Additional Information

- The database connection requires the SQL Server JDBC driver (`sqljdbc42.jar`). Ensure it is included in the classpath when running the program.
- Depending on your environment, you may need to adjust the database URL, username, and password parameters accordingly.

## Script for Compiling Java Files

You can use the following script to check for Java compiler availability and compile the files:

    ```vbscript
    Set objShell = CreateObject("WScript.Shell")
    objShell.Run "cmd /c cls", 0, True
    javacCheck = objShell.Run("javac -version", 0, True)
    
    If javacCheck = 0 Then
        objShell.Run "javac *.java", 0, True
    End If
    
    Set objShell = Nothing
    ```

This script checks if `javac` is available and compiles the Java files if it is. You can integrate it into your workflow for easier compilation.