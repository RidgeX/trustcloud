@echo off
call client -a res/Scc.png
call client -u users/A.crt
call client -u users/B.crt
call client -u users/C.crt
call client -u users/D.crt
call client -u users/E.crt
call client -u users/F.crt
call client -u users/G.crt
call client -u users/H.crt
call client -v res/Scc.png users/F.crt
call client -v users/D.crt users/H.crt
call client -v users/H.crt users/D.crt
call client -v users/G.crt users/H.crt
call client -v users/D.crt users/C.crt
call client -v users/C.crt users/D.crt
call client -v users/G.crt users/C.crt
call client -v users/G.crt users/F.crt
call client -v users/F.crt users/G.crt
call client -v users/C.crt users/B.crt
call client -v users/F.crt users/B.crt
call client -v users/F.crt users/E.crt
call client -v users/E.crt users/B.crt
call client -v users/B.crt users/A.crt
call client -v users/A.crt users/E.crt
call client -a res/hello.txt
call client -u users/I.crt
call client -v res/hello.txt users/I.crt
