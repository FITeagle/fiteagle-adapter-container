Compile library

	$ mvn compile
	
Compile tests
	
	$ mv test

Execute tests
	
	$ mvn exec:java -Dexec.mainClass=org.fiteagle.adapters.containers.Playground -Dexec.classpathScope=test