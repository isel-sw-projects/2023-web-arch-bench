@echo off
setlocal enabledelayedexpansion

:: Retrieve all relevant data (nginx and api) in one PowerShell call
for /f "usebackq tokens=1,2,3,4,5,6,7 delims=," %%i in (`powershell -Command "($config = Get-Content '../config/benchmark_setting.json' | ConvertFrom-Json); ($config.api.ports -join ';') + ',' + $config.api.project + ',' + $config.ngnix.path + ',' + $config.ngnix.conf + ',' + $config.ngnix.enable + ',' + $config.ngnix.port"`) do (
    set "apiPorts=%%i"
    set "apiProject=%%j"
    set "nginxPath=%%k"
    set "nginxConf=%%l"
    set "nginxEnable=%%m"
    set "nginxPort=%%n"
)

:: Replace semicolons in ports with spaces for easier iteration in the loop
set "apiPorts=%apiPorts:;= %"

:: Echo the results
:: echo API Ports: %apiPorts%
:: echo API Project: %apiProject%
:: echo NGINX Path: %nginxPath%
:: echo NGINX Conf: %nginxConf%
:: echo NGINX Enabled: %nginxEnable%
:: echo NGINX Port: %nginxPort%

:: Count the number of ports
set "portCount=0"
for %%p in (%apiPorts%) do (
    set /a portCount+=1
)

:: Validate if the ports make sense based on NGINX enable status
if /I "%nginxEnable%"=="False" (
    if "%portCount%" GTR "1" (
        echo Only one api port is allowed when NGINX is not enabled.
        exit /b 1
    )
)

:: Start the DB
echo Starting Database...
start cmd /c "cd /d ..\db-server && ..\gradlew.bat run"

:: Start the api and prepare the upstream_block for the nginx config
set "upstream_block="
for %%P in (%apiPorts%) do (
    echo Starting API on port %%P...
    start cmd /c "cd /d ..\%apiProject% && set PORT=%%P && ..\gradlew.bat run"
    set "upstream_block=!upstream_block!server 127.0.0.1:%%P; "
)

:: Wait until the api is up
:check_ports
for %%p in (%apiPorts%) do (
    echo Checking if service is up on port %%p...
    curl -s -o nul -w "%%{http_code}" http://localhost:%%p/api/ping > tmp.txt
    set /p http_code=<tmp.txt
    del tmp.txt

    echo HTTP Code: !http_code!
    
    if !http_code! == 200 (
        echo Service on port %%p is up!
        
    ) else (
        echo Service on port %%p is not up yet. HTTP Code: !http_code!
		goto retrying
    )
)
goto all_api_up

:retrying
echo Waiting 5 seconds before retrying...
timeout /t 5 /nobreak >nul
goto check_ports

:all_api_up

:: Create the nginx config and start the nginx
if /I "%nginxEnable%"=="True" (
	echo Checking if NGINX is running...
    nginx -t >nul 2>&1
    if !errorlevel! == 0 (
        echo NGINX is running. Stopping NGINX...
        cmd /c "cd /d %nginxPath% && .\nginx.exe -s stop"
        echo NGINX stopped.
    ) else (
        echo NGINX is not running.
    )

	powershell -Command ^
		"$template = Get-Content ../config/nginx.conf.template -Raw; " ^
		"$template = $template -replace '{{upstreamBlock}}', '!upstream_block!'; " ^
		"$template = $template -replace '{{nginxPort}}', '!nginxPort!'; " ^
		"$template | Set-Content %nginxConf%/nginx.conf"

	echo nginx.conf has been generated with the upstream block from config.json.

	echo Starting Nginx...
	echo path = %nginxPath%
	cmd /c "cd /d %nginxPath% && start nginx"

	echo Waiting for Nginx to initialize...
	timeout /t 5 /nobreak >nul
) 

:: Set portBenchmark based on NGINX enable status
if /I "%nginxEnable%"=="True" (
    set "portBenchmark=%nginxPort%"
    echo Using NGINX port for benchmark: !portBenchmark!
) else (
    :: Set portBenchmark to the API port
    for %%p in (%apiPorts%) do (
        set "portBenchmark=%%p"
    )
    echo Using API port for benchmark: !portBenchmark!
)

echo Starting Benchmark...
echo Port Benchmark: %portBenchmark%
start cmd /c "cd /d ..\benchmark && set PORT=%portBenchmark% && ..\gradlew.bat run"

echo All services started.
pause