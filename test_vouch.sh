#!/bin/bash
sh client.sh -a res/cloud.jpg
sh client.sh -u users/A.crt
sh client.sh -u users/B.crt
sh client.sh -u users/C.crt
sh client.sh -u users/D.crt
sh client.sh -u users/E.crt
sh client.sh -u users/F.crt
sh client.sh -u users/G.crt
sh client.sh -u users/H.crt
sh client.sh -v res/cloud.jpg users/F.crt
sh client.sh -v users/D.crt users/H.crt
sh client.sh -v users/H.crt users/D.crt
sh client.sh -v users/G.crt users/H.crt
sh client.sh -v users/D.crt users/C.crt
sh client.sh -v users/C.crt users/D.crt
sh client.sh -v users/G.crt users/C.crt
sh client.sh -v users/G.crt users/F.crt
sh client.sh -v users/F.crt users/G.crt
sh client.sh -v users/C.crt users/B.crt
sh client.sh -v users/F.crt users/B.crt
sh client.sh -v users/F.crt users/E.crt
sh client.sh -v users/E.crt users/B.crt
sh client.sh -v users/B.crt users/A.crt
sh client.sh -v users/A.crt users/E.crt
sh client.sh -a res/hello.txt
sh client.sh -u users/I.crt
sh client.sh -v res/hello.txt users/I.crt
