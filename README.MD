# University Secondary Term Software Project

## Running the Application

### Steps to Compile and Run

1. **Set Up Database Connection:**
   ```java
   String url = "jdbc:sqlserver://JoshiNitro5\\MSSQLSERVER02:1433;database=CinemaData";
   String username = "AdminCinema";
   ```

2. **Execute the Program:**
   ```sh
   java -cp ".;sqljdbc42.jar" Login
   ```

### VBScript to Compile and Run

```vbscript
Set objShell = CreateObject("WScript.Shell")

' Clear the console screen
objShell.Run "cmd /c cls", 0, True

' Check if javac is available
javacCheck = objShell.Run("javac -version", 0, True)

' If javac is available, compile *.java files
If javacCheck = 0 Then
    objShell.Run "javac *.java", 0, True
End If

' Run the Java program
objShell.Run "java -cp ""sqljdbc42.jar;"" Login", 0, True

Set objShell = Nothing
```