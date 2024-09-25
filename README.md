# 2023-web-arch-bench
Benchmarking web applications in different architectural variants.

## Overview
This repository contains the source code and scripts for my research project to obtain my **Master’s Degree** in **MEIC** at **ISEL**.

The testbed is designed to evaluate the performance of various JVM-based APIs, using NGINX as a reverse proxy to distribute requests across multiple instances. JMeter serves as the benchmarking tool, while the [TPC-C framework](https://www.tpc.org/tpcc/) is employed for benchmarking the APIs, providing a comprehensive analysis of their performance under different load conditions.

## Dissertation Structure
- **Institution**: Instituto Superior de Engenharia de Lisboa ([ISEL](https://www.isel.pt/))
- **Program**: Mestrado em Engenharia Informática e Computadores ([MEIC](https://www.isel.pt/curso/mestrado/mestrado-em-engenharia-informatica-e-de-computadores))
- **Title**: Benchmarking web applications in different architectural variants
- **Author**: José Cunha
- **Advisor**: Doutor Miguel Gamboa de Carvalho
  
# Getting started

## Requirements:
- Java 17 or sooner
- NGINX 1.25.4 or sooner

## Running
1. Adjust the configurations to fit your setup in the file [web-arch-bench/config/benchmark_setting.json](https://github.com/isel-sw-projects/2023-web-arch-bench/blob/main/web-arch-bench/config/benchmark_setting.json) **IMPORTANT**:IMPORTANT: If you are running with NGINX, make sure the paths and configuration path are correct.
    1. For NGINX, you can modify some values in its configuration file at [web-arch-bench/config/nginx.conf.template](https://github.com/isel-sw-projects/2023-web-arch-bench/blob/main/web-arch-bench/config/nginx.conf.template)
2. Run the project using one of the scripts available for your operating system in [web-arch-bench/scripts](https://github.com/isel-sw-projects/2023-web-arch-bench/tree/main/web-arch-bench/scripts)

## Notes
You may experience performance issues when running NGINX on Windows under high-load scenarios. As a workaround, you can use WSL or switch to a Linux environment.

