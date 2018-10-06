mkdir \target\classes
javac -cp ".;./jars/*" ./src/tachyon/*.java -d ./target/classes/
java -cp ".;./target/classes;./jars/*" tachyon.Tachyon -url https://storage.googleapis.com/vimeo-test/work-at-vimeo-2.mp4 -o ./work.mp4 -c 4
