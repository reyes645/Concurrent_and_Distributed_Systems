@echo off
for /L %%i in (1,1,10) do (
    start "Client %%i" cmd /c "java BookClient command_file.txt %%i"
)