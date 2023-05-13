build:
	javac -d . *.java Helpers/*.java
	jar -cvf BuildedFile.jar *.class Helpers/*.class
run:
	time java -cp BuildedFile.jar HP
clean:
	rm -rf *.class Helpers/*.class BuildedFile.jar