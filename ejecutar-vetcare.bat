@echo off
if not exist out mkdir out
javac -encoding UTF-8 -d out src\com\huellitas\vetcare\Main.java src\com\huellitas\vetcare\model\*.java src\com\huellitas\vetcare\service\*.java src\com\huellitas\vetcare\persistence\*.java src\com\huellitas\vetcare\ui\*.java
if errorlevel 1 pause
if errorlevel 1 exit /b
java -cp out com.huellitas.vetcare.Main
