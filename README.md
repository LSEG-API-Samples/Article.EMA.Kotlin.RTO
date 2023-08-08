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

The [How to Implement EMA Java Application with Kotlin Language](https://developers.refinitiv.com/en/article-catalog/article/how-to-implement-ema-java-application-with-kotlin-language) article shows how to implement Enterprise Message API (EMA) Java Consumer and Interactive Provider applications using Kotlin. This article shows a step-by-step guide to build the EMA Java Consumer application to connect and consume real-time streaming data from the Cloud (Refinitiv Real-Time Optimized, aka RTO).


Note: 
- This example project uses Kotlin version 1.9.0 and EMA Java 3.7.1.0 (RTSDK 2.1.1.L1)
- I am demonstrating with the Version 2 Authentication

## <a id="kotlin_overview"></a>Introduction to Kotlin

[Kotlin](https://kotlinlang.org/) is a modern, cross-platform, statically typed, high-level programming language developed by [Jetbrains](https://www.jetbrains.com/). The language syntax is concise, safe, interoperable with Java. Kotlin is designed with fully Java Interoperability in mind. It can be compiled to JavaScript and iOS/Android native code (via [LLVM](https://llvm.org/)) with many ways to reuse code between multiple platforms for productive programming.  

One major benefit for Java developers is integration with Java code and libraries (including RTSDK Java). Existing Java code can be called from Kotlin in a natural way. Kotlin syntax aims for reducing Java language verbosity and complexity, so Java developers can migrate to Kotlin easily. With a lot of potentials, Kotlin has been chosen by Google to be a first-class programming language on Android OS since 2019.

Kotlin syntax aims for "making developers happier" by reducing Java language verbosity and complexity like the following example:

``` kotlin

fun main() {
    println("Hello world!")
    val a = 100
    val b = 12
    println("$a + $b = ${sum(a,b)}") //"100 + 12 = 112"
}

fun sum(a: Int, b: Int): Int {
    return a + b
}
```

That’s all I have to say about Kotlin introduction

## <a id="prerequisite"></a>Prerequisite

Before I am going further, there is some prerequisite, dependencies, and libraries that the project is needed.

### Java SDK

Firstly, you need Java SDK. Please check for the supported Java version from the [API Compatibility Matrix](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java/documentation#api-compatibility-matrix) page. 

I am using the Open JDK version 11 in this project (as of April 2023).

### Maven

Next, the [Maven](https://maven.apache.org/) build automation tool. Please follow [Apache Maven installation guide document](https://maven.apache.org/install.html).

### Docker or IntelliJ IDEA

The example project is a console application that can be run on [Docker](https://www.docker.com/) or [IntelliJ IDEA](https://www.jetbrains.com/idea/) editor.

### Access to the RTO

This project uses RTO access credentials for both Version 1 Authentication (Machine ID type) and Version 2 Authentication (Service ID)

Please contact your Refinitiv representative to help you with the RTO account and services.

### Internet Access

This demonstration connects to RTO on AWS via a public internet.

## Implementation Detail 

### Maven pom.xml file

Let’s start with the Maven ```pom.xml``` file setting for Kotin. The ```pom.xml``` file the main Maven's project configuration. To use Kotlin with Maven, you need the **kotlin-maven-plugin** to compile Kotlin sources and modules. The first step is defining the version of Kotlin via the ```<properties>``` tag as follows:

```xml
<properties>
    <kotlin.compiler.jvmTarget>11</kotlin.compiler.jvmTarget>
    <rtsdk.version>3.7.1.0</rtsdk.version>
    <kotlin.version>1.9.0</kotlin.version>
    <main.class>com.refinitiv.kotlin.KonsumerRTOKt</main.class>
    ...
</properties>
```

And then set add the Kotlin standard library in the pom.xml file dependency setting.

```xml
<dependencies>
    <dependency>
        <groupId>com.refinitiv.ema
        </groupId>
        <artifactId>ema</artifactId>
        <version>${rtsdk.version}</version>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib</artifactId>
        <version>{kotlin.version}</version>
    </dependency>
</dependencies>
```

For more detail about EMA Java dependency and Maven, please check the [How to Set Up Refinitiv Real-Time SDK Java Application with Maven](https://developers.refinitiv.com/en/article-catalog/article/how-to-set-up-refinitiv-real-time-sdk-java-application-with-mave) article.

Next, set the pom.xml file's source directory and kotlin-maven-plugin plugins to let Maven knows where and how to compile the source code as follows:

```xml
<build>
    <sourceDirectory>src/main/kotlin</sourceDirectory>
    <testSourceDirectory>src/test/kotlin</testSourceDirectory>
    <plugins>
        <plugin>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-plugin</artifactId>
            <version>{kotlin.version}</version>
            <executions>
                <execution>
                    <id>compile</id>
                     <phase>compile</phase>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
                <execution>
                    <id>test-compile</id>
                    <phase>test-compile</phase>
                    <goals>
                        <goal>test-compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        ...
    <plugins>
</build>
```
You can see a full pom.xml file configurations in the project repository.

To learn more about Kotlin and Maven configurations, please see [Kotlin: Maven build tool](https://kotlinlang.org/docs/maven.html#enable-incremental-compilation) page.

## Consumer Application Code Walkthrough

The demo application (*KonsumerRTO.kt*) is based on the EMA Java ex451_MP_OAuth2Callback_V2, ex333_Login_Streaming_DomainRep, series300.ex360_MP_View and ex113_MP_SessionMgmt examples source code to connect and consume real-time streaming from RTO with the View feature.  

### Consumer Creation and Configuration

The **KonsumerRTO.kt** file implements the standard EMA Java Consumer applications with Kotlin syntax mindset. 

#### 1. Set the application entry point

An entry point of a Kotlin application is in the ```main``` function, and it does not need to be inside a class like Java. The ```main``` function creates the ```KonsumerRTO``` object, pass the RTO Service ID credential (Version 2 Authentication), and service name information to the ```KonsumerRTO``` object for further EMA-RTO workflow.

``` Java
import io.github.cdimascio.dotenv.dotenv

fun main() {
    val dotenv = dotenv {
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }
    val clientId: String = dotenv["CLIENT_ID"]
    val clientSecret: String = dotenv["CLIENT_SECRET"]
    val serviceName: String = dotenv["SERVICENAME"]

    val appRTO = KonsumerRTO()
    appRTO.run(clientId, clientSecret, serviceName)
}

class KonsumerRTO {

    fun run(clientId: String, clientSecret: String, serviceName: String = "ELEKTRON_DD") {
        //Perform RTO connection logic
    }
}
```

The code use the [dotenv-kotlin](https://github.com/cdimascio/dotenv-kotlin) library to load the RTO credentials and configuration from the environment variable ```.env``` file or the System Environment Variables. To use the dotenv-kotlin library, you just import the ```import io.github.cdimascio.dotenv.dotenv``` package and create the ```dotenv``` object via the ```val dotenv = dotenv {}``` wtih ```ignoreIfMalformed = true``` and ```ignoreIfMissing = true``` properties to populate configurations. After that you can access both system environment variables and ```.env```'s configurations from the ```dotenv.get("...");``` 

Please note that the OS/system's environment variables always override ```.env``` configurations by default. 

### 2. Setting RDP Version 2 Authentication credentials to the OmmConsumer Class

The next step is creating the ```OmmConsumer``` object. Then set the V2 auth Client Credentials (client ID and client secret) to the ```OmmConsumer``` instance via the *OmmConsumerConfig* class. 

``` Java
class KonsumerRTO {

    private val tokenUrlV2 = "https://api.refinitiv.com/auth/oauth2/v2/token"
    private val itemName = "EUR="

    fun run(clientId: String, clientSecret: String, serviceName: String = "ELEKTRON_DD") {

        var consumer: OmmConsumer? = null

        try {
            val appClient = AppClient()
            val config: OmmConsumerConfig = EmaFactory.createOmmConsumerConfig()

            consumer = EmaFactory.createOmmConsumer(
                config.consumerName("Consumer_4")
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .tokenServiceUrlV2(tokenUrlV2)
            )

        } catch (excp: InterruptedException) {
            println(excp.message)
        } catch (ex: OmmException) {
            println(ex.message)
        } finally {
            consumer?.uninitialize();
        }
    }
}
```
The "Consumer_4" is defined to connect to RTO in the EmaConfig.xml file as follows

```xml
<Channel>
	<Name value="Channel_4"/>
	<ChannelType value="ChannelType::RSSL_ENCRYPTED"/>
	<CompressionType value="CompressionType::None"/>
	<GuaranteedOutputBuffers value="5000"/>
	<!-- EMA discovers a host and a port from RDP service discovery for the specified location
		 when both of them are not set and the session management is enable. -->
	<Location value="ap-southeast"/>
	<EnableSessionManagement value="1"/>
	<ObjectName value=""/>
</Channel>
```

### 2.  Setting a client secret in the ReactorOAuthCredentialRenewal

By default, the EMA API will store all credential information. To use secure credential storage, a callback function can be specified by the user. If an ```OmmOAuth2ConsumerClient``` instance is specified when creating the OmmConsumer object, the EMA API does not store the password or clientSecret. In this case, the application must supply the password or clientSecret whenever the OAuth credential event ```OmmOAuth2ConsumerClient.onCredentialRenewal``` callback method is invoked. This call back must call set the credentials to the ```OAuth2CredentialRenewal``` instance and set it to the ```OmmConsumer.renewOAuthCredentials``` method to provide the updated credentials.

```Java

/* This is for example purposes, For best security, please use a proper credential store. */
data class CredentialStore(var clientSecret: String, var clientId: String, var consumer: OmmConsumer?)

/* Implementation of OmmOAuth2ConsumerClient.  This is a very basic callback that uses the closure to obtain
 * the OmmConsumer and call submitOAuthCredentialRenewal.
 * 
 * This is intended to show functionality, so this example does not implement or use secure credential storage.
 */
class OAuthcallback : OmmOAuth2ConsumerClient {
    override fun onOAuth2CredentialRenewal(event: OmmConsumerEvent?) {
        val credentials: CredentialStore? = event?.closure() as CredentialStore?
        val renewal = EmaFactory.createOAuth2CredentialRenewal() as OAuth2CredentialRenewal

        renewal.clientId(credentials?.clientId)
        renewal.clientSecret(credentials?.clientSecret)

        println("Submitting credentials due to token renewal")

        credentials?.consumer?.renewOAuthCredentials(renewal)

    }
}

```

The main purpose of the ```CredentialStore``` class is for holding the credentials only, so I am using the Kotlin [Data Class](https://kotlinlang.org/docs/data-classes.html) feature to shorten this POJO class implementation.

Then add the ```oAuthCallback``` and ```credentials``` objects to the ```OmmConsumer.createOmmConsumer(OmmConsumerConfig config,OmmOAuth2ConsumerClient OAuthClient,java.lang.Object closure)``` method as follows.

```Java
fun run(clientId: String, clientSecret: String, serviceName: String = "ELEKTRON_DD") {

    var consumer: OmmConsumer? = null
    val oAuthCallback = OAuthcallback()
    val credentials = CredentialStore(clientId, clientSecret, consumer)

    try {
        val appClient = AppClient()
        val config: OmmConsumerConfig = EmaFactory.createOmmConsumerConfig()

        consumer = EmaFactory.createOmmConsumer(
            config.consumerName("Consumer_4")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tokenServiceUrlV2(tokenUrlV2), oAuthCallback, credentials as Any
        )
        credentials.consumer = consumer
    }
    ...
}

```

### 3.  Registering Login Stream

When connecting to RTO, it would be nice to monitors the state of connectivity. The application can open a login stream to receive the stream's status and information.

```Java

fun run(clientId: String, clientSecret: String, serviceName: String = "ELEKTRON_DD") {

    var consumer: OmmConsumer? = null
    val oAuthCallback = OAuthcallback()
    val credentials = CredentialStore(clientId, clientSecret, consumer)

    try {
        val appClient = AppClient()
        val config: OmmConsumerConfig = EmaFactory.createOmmConsumerConfig()

        consumer = EmaFactory.createOmmConsumer(
            config.consumerName("Consumer_4")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tokenServiceUrlV2(tokenUrlV2), oAuthCallback, credentials as Any
        )
        credentials.consumer = consumer

        val loginReq = EmaFactory.Domain.createLoginReq()
        consumer.registerClient(loginReq.message(), appClient)
    }
    ...
}
```

### 4.  Requesting Data

```Java
fun run(clientId: String, clientSecret: String, serviceName: String = "ELEKTRON_DD") {
    ...
    try {
        val appClient = AppClient()
        val config: OmmConsumerConfig = EmaFactory.createOmmConsumerConfig()
        ...

        // --------------------------------- View  ---------------------------
        val view = EmaFactory.createElementList()
        val arrayView = EmaFactory.createOmmArray()

        arrayView.fixedWidth(2)
        arrayView.add(EmaFactory.createOmmArrayEntry().intValue(22)) //BID
        arrayView.add(EmaFactory.createOmmArrayEntry().intValue(25)) //ASK
        arrayView.add(EmaFactory.createOmmArrayEntry().intValue(15)) //CURRENCY
        arrayView.add(EmaFactory.createOmmArrayEntry().intValue(875)) //VALUE_DT1
        arrayView.add(EmaFactory.createOmmArrayEntry().intValue(1010)) //VALUE_TS1

        view.add(EmaFactory.createElementEntry().uintValue(EmaRdm.ENAME_VIEW_TYPE, 1))
        view.add(EmaFactory.createElementEntry().array(EmaRdm.ENAME_VIEW_DATA, arrayView))

        consumer.registerClient(
            EmaFactory.createReqMsg().serviceName(serviceName).payload(view).name(itemName),
            appClient
        )
        Thread.sleep(900000)
    }
    ...
}
```

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




