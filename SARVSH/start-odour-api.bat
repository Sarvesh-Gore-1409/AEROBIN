@echo off
echo Starting AeroBins Odour API Server...
call .venv\Scripts\activate.bat
python scripts\api_server.py
pause
