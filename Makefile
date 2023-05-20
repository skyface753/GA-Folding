build:
	javac -d . *.java Helpers/*.java
	jar -cvf BuildedFile.jar *.class Helpers/*.class
run:
	time java -cp BuildedFile.jar HP

run-prakt-2:
	time java -cp BuildedFile.jar HP -c false -i true -g 100 -n 100

run-prakt-3:
	time java -cp BuildedFile.jar HP -i true -g 100 -n 100
clean:
	rm -rf *.class Helpers/*.class BuildedFile.jar