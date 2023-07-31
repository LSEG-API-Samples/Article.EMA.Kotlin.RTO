package com.refinitiv.kotlin

import com.refinitiv.ema.access.Msg
import com.refinitiv.ema.access.AckMsg
import com.refinitiv.ema.access.GenericMsg
import com.refinitiv.ema.access.RefreshMsg
import com.refinitiv.ema.access.ReqMsg
import com.refinitiv.ema.access.StatusMsg
import com.refinitiv.ema.access.UpdateMsg
import com.refinitiv.ema.access.EmaFactory
import com.refinitiv.ema.access.OmmConsumer
import com.refinitiv.ema.access.OmmConsumerClient
import com.refinitiv.ema.access.OmmConsumerConfig
import com.refinitiv.ema.access.OmmConsumerEvent
import com.refinitiv.ema.access.OmmException

class AppKlient: OmmConsumerClient {
    override fun onRefreshMsg(refreshMsg: RefreshMsg?, event: OmmConsumerEvent?) {
        println(refreshMsg)
    }

    override fun onUpdateMsg(updateMsg: UpdateMsg?, event: OmmConsumerEvent?) {
        println(updateMsg)
    }

    override fun onStatusMsg(statusMsg: StatusMsg?, event: OmmConsumerEvent?) {
        println(statusMsg)
    }

    override fun onAckMsg(ackMsg: AckMsg?, event: OmmConsumerEvent?) {
        println(ackMsg)
    }

    override fun onAllMsg(allMsg: Msg?, event: OmmConsumerEvent?) {
        println(allMsg)
    }

    override fun onGenericMsg(genericMsg: GenericMsg?, event: OmmConsumerEvent?) {
        println(genericMsg)
    }
}

fun main(){
        var consumer : OmmConsumer? = null
        try {
            val appKlient: AppKlient = AppKlient()
            val config: OmmConsumerConfig = EmaFactory.createOmmConsumerConfig()

            consumer = EmaFactory.createOmmConsumer(config.host("localhost:14002").username("wasin"))

            val reqMsg: ReqMsg = EmaFactory.createReqMsg()

            consumer.registerClient(reqMsg.serviceName("ELEKTRON_DD").name("JPY="), appKlient)

            Thread.sleep(600000)
        } catch (excp: InterruptedException ) {
           println(excp.message)
        } catch (ex: OmmException){
            println(ex.message)
        }
        finally {
            consumer?.uninitialize();
        }
    }
