#!/bin/bash
echo Create file:
sh client.sh -a res/hello.txt
echo

echo Fetch file:
sh client.sh -f hello.txt
echo

echo Upload certificate:
sh client.sh -u res/test.crt
echo

echo Fetch certificate:
sh client.sh -f test.crt
echo

echo Vouch for file:
sh client.sh -v res/hello.txt res/test.crt
echo

echo List directory:
sh client.sh -l
