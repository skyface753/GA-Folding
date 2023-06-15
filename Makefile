build:
	javac -d . *.java Helpers/*.java
	jar -cvf BuildedFile.jar *.class Helpers/*.class
run:
	time java -cp BuildedFile.jar HP

run-prakt-2:
	time java -cp BuildedFile.jar HP --crossover false --image true --generations 100 --population 100

run-prakt-3:
	time java -cp BuildedFile.jar HP --image true --generations 100 --population 100
run-prakt-4:
	time java -cp BuildedFile.jar HP --image false --generations 100 --population 100 --scalemutate true --tunier true --mutationrate 0.1

run-test-all-params-default:
	time java -cp BuildedFile.jar HP --crossover true --image false --generations 100 --population 100 --mutationrate 0.01 --scalemutate false --tunier false --sigmascaling false --elitism false
run-test-all-params-changed:
	time java -cp BuildedFile.jar HP --crossover false --image true --generations 1000 --population 1000 --mutationrate 0.1 --scalemutate true --tunier true --sigmascaling true --elitism true
clean:
	rm -rf *.class Helpers/*.class BuildedFile.jar