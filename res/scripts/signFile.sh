openssl sha1 -sign test.key -out hello.rsa hello.txt
openssl sha1 -verify test.pub.key -signature hello.rsa hello.txt
