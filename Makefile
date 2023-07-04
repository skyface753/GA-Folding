build:
	javac -d . *.java Helpers/*.java
	jar -cvf BuildedFile.jar *.class Helpers/*.class
run:
	time java -cp BuildedFile.jar HP

run-prakt-2: lib1.a
	time java -cp BuildedFile.jar HP --crossover false --image true --generations 100 --population 100

run-prakt-3: lib1.a
	time java -cp BuildedFile.jar HP --image true --generations 100 --population 100
run-prakt-4: lib1.a
	time java -cp BuildedFile.jar HP --image false --generations 200 --population 200 --scalemutate true --tunier true --mutationrate 0.1

# 60er Sequenz, 20/30 sekunden, fitness
run-seq-60: lib1.a
	time java -cp BuildedFile.jar HP --image false --generations 100 --population 2000 --scalemutate false --tunier false --mutationrate 0.01 --elitism false --scalemutate true

run-test-all-params-default: lib1.a
	time java -cp BuildedFile.jar HP --crossover true --image false --generations 100 --population 100 --mutationrate 0.01 --scalemutate false --tunier false --sigmascaling false --elitism false
run-test-all-params-changed: lib1.a
	time java -cp BuildedFile.jar HP --crossover false --image true --generations 1000 --population 1000 --mutationrate 0.1 --scalemutate true --tunier true --sigmascaling true --elitism true
clean:
	rm -rf *.class Helpers/*.class BuildedFile.jar
	rm -rf /tmp/ga/*

lib1.a: relay

.PHONY: relay
relay:
	javac -d . *.java Helpers/*.java
	jar -cvf BuildedFile.jar *.class Helpers/*.class