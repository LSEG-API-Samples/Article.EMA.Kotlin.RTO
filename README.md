## Implementing RTO Consumer Application with Kotlin
- version: 1.0.0
- Last Update: August 2023
- Environment: Docker or IntelliJ IDEA
- Compiler: Kotlin
- Prerequisite: [Demo prerequisite](#prerequisite)

Example Code Disclaimer:
ALL EXAMPLE CODE IS PROVIDED ON AN “AS IS” AND “AS AVAILABLE” BASIS FOR ILLUSTRATIVE PURPOSES ONLY. REFINITIV MAKES NO REPRESENTATIONS OR WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED, AS TO THE OPERATION OF THE EXAMPLE CODE, OR THE INFORMATION, CONTENT, OR MATERIALS USED IN CONNECTION WITH THE EXAMPLE CODE. YOU EXPRESSLY AGREE THAT YOUR USE OF THE EXAMPLE CODE IS AT YOUR SOLE RISK.

## <a id="intro"></a>Introduction

[Refinitiv Real-Time SDK (Java Edition)](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java) (RTSDK, formerly known as Elektron SDK) is a suite of modern and open source APIs that aim to simplify development through a strong focus on ease of use and standardized access to a broad set of Refinitiv proprietary content and services via the proprietary TCP connection named RSSL and proprietary binary message encoding format named OMM Message. The capabilities range from low latency/high-performance APIs right through to simple streaming Web APIs. 

The [How to Implement EMA Java Application with Kotlin Language](https://developers.refinitiv.com/en/article-catalog/article/how-to-implement-ema-java-application-with-kotlin-language) article shows how to implement Enterprise Message API (EMA) Java Consumer and Interactive Provider applications using Kotlin. This article shows a step-by-step guide to built the EMA Java Consumer application to connect and consume real-time streaming data from the Cloud (Refinitiv Real-Time Optimized, aka RTO).


Note: This example project uses Kotlin version 1.9.0 and EMA Java 3.7.1.0 (RTSDK 2.1.1.L1)

## <a id="kotlin_overview"></a>Kotlin Overview

[Kotlin](https://kotlinlang.org/) is a modern, cross-platform, statically-typed, high-level programming language developed by [Jetbrains](https://www.jetbrains.com/). The language syntax is concise, safe, interoperable with Java. Kotlin is designed with fully Java Interoperability in mind, and also can be compiled to JavaScript and iOS/Android native code (via [LLVM](https://llvm.org/)) with many ways to reuse code between multiple platforms for productive programming.  

One major benefit for Java developers is integration with Java code and libraries (including RTSDK Java). Existing Java code can be called from Kotlin in a natural way. Kotlin syntax aims for reducing Java language verbosity and complexity, so Java developers can migrate to Kotlin easily. With a lot of potentials, Kotlin has been chosen by Google to be a first-class programming language on Android OS since 2019.

## <a id="prerequisite"></a>Prerequisite

Before I am going further, there is some prerequisite, dependencies, and libraries that the project is needed.

TBD

## <a id ="how_to_run"></a>How to run the demo application

### Maven

``` bash
mvn clean

mvn package

java -cp .;target/RTO_Kotlin-1.0-jar-with-dependencies.jar com.refinitiv.kotlin.KonsumerRTOKt
```

### Docker

Create a file name ```.env``` with the following content

``` ini
#Authentication V2
CLIENT_ID=<Client ID V2>
CLIENT_SECRET=<Client Secret V2>
SERVICENAME=<ELEKTRON_DD or ERT_FD3_LF1>
```

and then run

``` bash
docker build . -t kotlin_rto

docker run -it --name kotlin_rto --env-file .env kotlin_rto
```

## <a id="ref"></a>References

For further details, please check out the following resources:
* [Refinitiv Real-Time SDK Java page](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java) on the [Refinitiv Developer Community](https://developers.refinitiv.com/) web site.
* [Refinitiv Real-Time SDK Family](https://developers.refinitiv.com/en/use-cases-catalog/refinitiv-real-time) page.
* [Enterprise Message API Java Quick Start](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java/quick-start)
* [Developer Webinar: Introduction to Enterprise App Creation With Open-Source Enterprise Message API](https://www.youtube.com/watch?v=2pyhYmgHxlU)
* [Developer Article: 10 important things you need to know before you write an Enterprise Real Time application](https://developers.refinitiv.com/article/10-important-things-you-need-know-you-write-elektron-real-time-application)
* [Changes to Customer Access and Identity Management: Refinitiv Real-Time - Optimized](https://developers.refinitiv.com/en/article-catalog/article/changes-to-customer-access-and-identity-management--refinitiv-re)
* [Kotlin programming language: Official page](https://kotlinlang.org).
* [Kotlin Getting Started Guide](https://kotlinlang.org/docs/getting-started.html)
* [Kotlin comparison to Java](https://kotlinlang.org/docs/comparison-to-java.html)
* [Get started with Kotlin/JVM](https://kotlinlang.org/docs/jvm-get-started.html)
* [Kotlin Build tools: Maven](https://kotlinlang.org/docs/maven.html#specifying-compiler-options)
* [IntelliJ IDEA: Get started with Kotlin](https://www.jetbrains.com/help/idea/get-started-with-kotlin.html)
* [How to Implement EMA Java Application with Kotlin Language](https://developers.refinitiv.com/en/article-catalog/article/how-to-implement-ema-java-application-with-kotlin-language) article


For any question related to this article or the RTSDK page, please use the Developer Community [Q&A Forum](https://community.developers.refinitiv.com/).




