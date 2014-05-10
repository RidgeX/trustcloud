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
call client -v Scc.png users/F.crt
call client -v D.crt users/H.crt
call client -v H.crt users/D.crt
call client -v G.crt users/H.crt
call client -v D.crt users/C.crt
call client -v C.crt users/D.crt
call client -v G.crt users/C.crt
call client -v G.crt users/F.crt
call client -v F.crt users/G.crt
call client -v C.crt users/B.crt
call client -v F.crt users/B.crt
call client -v F.crt users/E.crt
call client -v E.crt users/B.crt
call client -v B.crt users/A.crt
call client -v A.crt users/E.crt
call client -a res/hello.txt
call client -u users/I.crt
call client -v hello.txt users/I.crt
