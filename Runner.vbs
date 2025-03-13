Set objShell = CreateObject("WScript.Shell")

' Compile all Java files
objShell.Run "javac *.java", 0, True

' Run the Login class with the MySQL connector
objShell.Run "java -cp "".;mysql-connector-j-8.4.0.jar"" Login", 0, True