@echo off
java -cp bin/;lib/bcpkix-jdk15on-150.jar;lib/bcprov-jdk15on-150.jar;lib/guava-17.0.jar;lib/java-getopt-1.0.14.jar cits3002.util.KeyCreator %*
