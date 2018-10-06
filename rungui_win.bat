mkdir target_gui\classes
javac -cp ".;./jars/*;./jars_win/*" ./src/tachyon/*.java ./src/gui/*.java -d ./target_gui/classes/
java -cp ".;./target_gui/classes;./jars/*;./jars_win/*" gui.TachyonGUI