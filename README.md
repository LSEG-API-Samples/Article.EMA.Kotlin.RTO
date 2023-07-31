## Implementing RTO Consumer Application with Kotlin

https://kotlinlang.org/docs/maven.html#specifying-compiler-options

https://kotlinlang.org/docs/kotlin-tour-hello-world.html

https://kotlinlang.org/docs/kotlin-tour-functions.html

https://www.jetbrains.com/help/idea/create-your-first-kotlin-app.html#run-the-jar

java -cp .;target/RTO_Kotlin-1.0-jar-with-dependencies.jar com.refinitiv.kotlin.KonsumerKt

java -cp .;target/RTO_Kotlin-1.0-jar-with-dependencies.jar com.refinitiv.kotlin.KonsumerRTOKt

docker build . -t kotlin_rto

docker run -it --name kotlin_rto --env-file .env kotlin_rto