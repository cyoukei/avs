@echo off
set BASE_PATH=V:\AVS

cd %BASE_PATH%
play start --%prod%

cd %BASE_PATH%\nginx-1.14.0
nginx -s start



