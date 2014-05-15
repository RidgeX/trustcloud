@echo off
echo Create file:
call client -a res/hello.txt
echo.

echo Fetch file:
call client -f hello.txt
echo.

echo Upload certificate:
call client -u res/test.crt
echo.

echo Fetch certificate:
call client -f test.crt
echo.

echo Vouch for file:
call client -v res/hello.txt res/test.crt
echo.

echo List directory:
call client -l
