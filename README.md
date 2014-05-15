# Readme

## Libraries
  * [Bouncy Castle](http://www.bouncycastle.org/java.html)
  * [GNU getopt - Java port](http://www.urbanophile.com/arenn/hacking/download.html)
  * [Google Guava](http://code.google.com/p/guava-libraries/)

## Build instructions
  * Build the project using `make`.
  * The server can be run using `./server.sh [port]`.
  * The client can be run using `./client.sh [options]`.

## Testing
  * The client can work with X.509 certificates and PKCS#8 keys generated by OpenSSL (see `res/scripts/createKeys.sh`) or by the included `./keyCreate.sh` utility.
  * Running `./test.sh` performs some basic functionality tests.
  * Running `./testVouch.sh` constructs an example trust graph for checking that the ring verifier works.

## Other notes
  * `res/` contains some example data files (text and images).
  * `users/` contains certificates and keys for nine example users.
  * The client/server protocol is documented in `protocol.txt`.
