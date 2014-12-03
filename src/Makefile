JCC = javac
CC = gcc
J_HEADERS =  ./include/
JUNIT_PATH = .:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar
HAMCREST_PATH = /opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar
LIB_PATH= -Djava.library.path=./

lib:header
	$(CC) -shared -fpic -o libcencryption.so -I $(J_HEADERS) -I $(J_HEADERS)linux/ cencryption.c

header:classes
	javah -jni common.Encryption

classes:
	$(JCC) -cp $(JUNIT_PATH) ./*/*.java

tests:lib
	java $(LIB_PATH) -classpath "$(JUNIT_PATH):.:$(HAMCREST_PATH)" org.junit.runner.JUnitCore tests.testEncryption

runServer:lib
	java $(LIB_PATH) -classpath . Server.Main

runClient:lib
	java $(LIB_PATH) -classpath . Client.Main

clean:
	rm -rf *.class libcencryption.so