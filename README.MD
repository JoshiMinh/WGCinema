# University Secondary Term Software Project

## Running the Application

### Steps to Compile and Run:

1. **Prepare Java Files:**
   - Ensure all necessary Java files are in the directory.

2. **Open Command Line Interface:**

3. **Compile Java Files:**
   ```sh
   javac *.java
   ```

4. **Set Up Database Connection:**
   ```java
   String url = "jdbc:sqlserver://JoshiNitro5\\MSSQLSERVER02:1433;database=CinemaData";
   String username = "AdminCinema";
   String password = "Admin";
   ```

5. **Execute the Program:**
   ```sh
   java -cp ".;sqljdbc42.jar" Login
   ```

### Additional Information:

- **JDBC Driver:** Ensure the SQL Server JDBC driver (`sqljdbc42.jar`) is in the classpath.
- **Adjust Parameters:** Modify the database URL, username, and password as needed for your environment.

## Script for Compiling Java Files

Use this script to check for Java compiler availability and compile the files:

```vbscript
Set objShell = CreateObject("WScript.Shell")
objShell.Run "cmd /c cls", 0, True
javacCheck = objShell.Run("javac -version", 0, True)

If javacCheck = 0 Then
    objShell.Run "javac *.java", 0, True
End If

Set objShell = Nothing
```

This script ensures `javac` is available and compiles the Java files if it is. Integrate it into your workflow for easier compilation.