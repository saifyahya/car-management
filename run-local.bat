@echo off
start "Valet Backend" cmd /k "cd backend && mvn spring-boot:run"
cd frontend
call npm install --no-audit --no-fund
call npm start
