openssl genrsa -out test.key 1024
openssl req -new -x509 -key test.key -out test.crt -days 365
openssl rsa -in test.key -pubout -out test.pub.key

openssl rsa -in test.key -check
openssl x509 -in test.crt -text -noout
