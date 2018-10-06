# Tachyon
A multipart file downloader (aka. download accelerator)

## Building and Running:
Make sure you're in the project directory<br>

Windows:
```
mkdir \target\classes
javac -cp ".;./jars/*" ./src/tachyon/*.java -d ./target/classes/
java -cp ".;./target/classes;./jars/*" tachyon.Tachyon -url <file url> -o <path to output file> -c <max number of connections>
```

Linux:
```
mkdir -p \target\classes
javac -cp ".:./jars/*" ./src/tachyon/*.java -d ./target/classes/
java -cp ".:./target/classes:./jars/*" tachyon.Tachyon -url <file url> -o <path to output file> -c <max number of connections>
```

#### or you can run run.sh (for linux) or run.bat (windows) which should try to download the sample file provided using 4 connections and save it in the same directory

## Documentation
check out the project's [Wiki](https://github.com/sam46/Tachyon/wiki)
