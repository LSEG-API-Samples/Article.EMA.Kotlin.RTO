///*|----------------------------------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license                             --
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.                         --
// *|                See the project's LICENSE.md for details.                                         --
// *|           Copyright (C) 2020-2023 Refinitiv. All rights reserved.                                --
///*|----------------------------------------------------------------------------------------------------

package com.refinitiv.kotlin

import com.refinitiv.ema.access.*
import com.refinitiv.ema.domain.login.Login.LoginRefresh
import com.refinitiv.ema.domain.login.Login.LoginStatus
import com.refinitiv.ema.rdm.EmaRdm
import io.github.cdimascio.dotenv.dotenv

class KonsumerRTO {

    private val tokenUrlV2 = "https://api.refinitiv.com/auth/oauth2/v2/token"
    private val itemName = "EUR="

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

            // --------------------------------- View  ---------------------------
            val view = EmaFactory.createElementList()
            val arrayView = EmaFactory.createOmmArray()

            arrayView.fixedWidth(2)
            arrayView.add(EmaFactory.createOmmArrayEntry().intValue(22)) //BID
            arrayView.add(EmaFactory.createOmmArrayEntry().intValue(25)) //ASK
            arrayView.add(EmaFactory.createOmmArrayEntry().intValue(15)) //CURRENCY
            arrayView.add(EmaFactory.createOmmArrayEntry().intValue(875)) //CURRENT DATE
            arrayView.add(EmaFactory.createOmmArrayEntry().intValue(1010)) //CURRENT DATE

            view.add(EmaFactory.createElementEntry().uintValue(EmaRdm.ENAME_VIEW_TYPE, 1))
            view.add(EmaFactory.createElementEntry().array(EmaRdm.ENAME_VIEW_DATA, arrayView))

            consumer.registerClient(EmaFactory.createReqMsg().serviceName(serviceName).payload(view).name(itemName), appClient)
            Thread.sleep(900000)
        } catch (excp: InterruptedException) {
            println(excp.message)
        } catch (ex: OmmException) {
            println(ex.message)
        } finally {
            consumer?.uninitialize();
        }
    }
}


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

data class CredentialStore(var clientSecret: String, var clientId: String, var consumer: OmmConsumer?)

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

class AppClient : OmmConsumerClient {

    private val _loginRefresh: LoginRefresh = EmaFactory.Domain.createLoginRefresh()
    private val _loginStatus: LoginStatus = EmaFactory.Domain.createLoginStatus()

    override fun onRefreshMsg(refreshMsg: RefreshMsg, event: OmmConsumerEvent) {
        println("Received Refresh. Item Handle: ${event.handle()} Closure: ${event.closure()}")
        println("Item Name: ${if (refreshMsg.hasName()) refreshMsg.name() else "<not set>"}")
        println("Service Name: ${if (refreshMsg.hasServiceName()) refreshMsg.serviceName() else "<not set>"}")
        println("Item State: ${refreshMsg.state()}")

        if (refreshMsg.dataType() == EmaRdm.MMT_LOGIN) {
            _loginRefresh.clear()
            println(_loginRefresh.message(refreshMsg).toString())
        } else {
            decode(refreshMsg)
        }
        println()
    }

    override fun onUpdateMsg(updateMsg: UpdateMsg, event: OmmConsumerEvent) {
        println("Received Update. Item Handle: ${event.handle()} Closure: ${event.closure()}")
        println("Item Name: ${if (updateMsg.hasName()) updateMsg.name() else "<not set>"}")
        println("Service Name: ${if (updateMsg.hasServiceName()) updateMsg.serviceName() else "<not set>"}")

        decode(updateMsg)

        println()
    }

    override fun onStatusMsg(statusMsg: StatusMsg, event: OmmConsumerEvent) {
        println("Received Status. Item Handle: ${event.handle()} Closure: ${event.closure()}")
        println("Item Name: ${if (statusMsg.hasName()) statusMsg.name() else "<not set>"}")
        println("Service Name: ${if (statusMsg.hasServiceName()) statusMsg.serviceName() else "<not set>"}")

        if (statusMsg.hasState()) println("Item State ${statusMsg.state()}")

        if (statusMsg.domainType() == EmaRdm.MMT_LOGIN) {
            _loginStatus.clear()
            println(_loginStatus.message(statusMsg).toString())
        }

        println()

    }

    override fun onAckMsg(ackMsg: AckMsg, event: OmmConsumerEvent) {}

    override fun onAllMsg(allMsg: Msg, event: OmmConsumerEvent) {}

    override fun onGenericMsg(genericMsg: GenericMsg, event: OmmConsumerEvent) {}

    private fun decode(msg: Msg) {

        if (msg.attrib().dataType() == DataType.DataTypes.FIELD_LIST) decode(msg.attrib().fieldList())

        if (msg.payload().dataType() == DataType.DataTypes.FIELD_LIST) decode(msg.payload().fieldList())
    }


    private fun decode(fieldList: FieldList) {
        for (fieldEntry: FieldEntry in fieldList) {
            print(
                "Fid ${fieldEntry.fieldId()} Name = ${fieldEntry.name()} DataType: ${DataType.asString(fieldEntry.load().dataType())} Value: "
            )
            if (Data.DataCode.BLANK == fieldEntry.code()) {
                println(" blank")
            } else {
                when (fieldEntry.loadType()) {
                    DataType.DataTypes.REAL -> println(fieldEntry.real().asDouble())
                    DataType.DataTypes.TIME -> println(
                        "${fieldEntry.time().hour()} : ${fieldEntry.time().minute()} : ${fieldEntry.time().second()} : ${fieldEntry.time().millisecond()}"
                    )
                    DataType.DataTypes.DATE -> println(
                        "${fieldEntry.date().day()} / ${fieldEntry.date().month()} / ${fieldEntry.date().year()}"
                    )
                    DataType.DataTypes.ENUM -> println(if (fieldEntry.hasEnumDisplay()) fieldEntry.enumDisplay() else fieldEntry.enumValue())
                    DataType.DataTypes.ERROR -> println(
                        "${fieldEntry.error().errorCode()} (${fieldEntry.error().errorCodeAsString()})"
                    )
                    else -> println()
                }
            }
        }
    }
}