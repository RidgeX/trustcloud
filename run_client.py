#!/usr/bin/python
import os

os.chdir('bin')
os.system('java -cp ../lib/guava-17.0.jar:../lib/bcpkix-jdk15on-150.jar:../lib/bcprov-jdk15on-150.jar:. cits3002.client.Client')
