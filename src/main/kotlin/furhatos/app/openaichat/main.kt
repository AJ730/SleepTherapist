package furhatos.app.openaichat

import furhatos.app.openaichat.flow.*
import furhatos.app.openaichat.flow.chatbot.SocketHandler
import furhatos.app.openaichat.flow.chatbot.msocket
import furhatos.flow.kotlin.*
import furhatos.nlu.LogisticMultiIntentClassifier
import furhatos.nlu.NullIntent
import furhatos.nlu.Response
import furhatos.skills.Skill
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Future

class OpenaichatSkill : Skill() {
    override fun start() {
        Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    LogisticMultiIntentClassifier.setAsDefault()
    Skill.main(args)

    Furhat.turnTakingPolicy = object : TurnTakingPolicy {
        override fun turnYieldTimeout(response: Response<*>): Int {
            if (response.speech.length < 1000) {
                // The response was less than 1000ms long, let's give the user 2000ms more to continue speaking
                return 2000
            } else {
                return 0
            }
        }
    }
}
//    SocketHandler
//    SocketHandler.setSocket()
//    SocketHandler.establishConnection()
//    val client = SocketHandler.getSocket()
//    client.emit("request", true )
//
//    for (i in 1..100000){
//        client.on("emotion") { a ->
//
//
//            if (a[0] != null) {
//                val data = a[0] as String
//
//                println(data)
//            }
//        }
//    }
//
//}


//
//
//fun main(args: Array<String>) {
//
////    var client = SocketChannel.open(
////        InetSocketAddress("localhost", 2004))
////    client.configureBlocking(true)
////    var buffer = ByteBuffer.allocate(1024);
////    client.read(buffer)
////    var msg = ""
////    if(buffer.remaining() != -1) {
////        val curr = String(buffer.array(), StandardCharsets.UTF_8)
////        print(curr)
////        if(curr.matches("^[a-zA-Z]*$".toRegex())){
////            msg = curr;
////        }
////    }
////
////    print(msg)
//
//    SocketHandler.setSocket()
//    SocketHandler.establishConnection()
//
//    val mSocket = SocketHandler.getSocket()
//
//    mSocket.on("emotion") { a ->
//        if (a[0] != null) {
//            val counter = a[1] as String
//            print(counter)
//
//        }
//    }
//
//    print("hello")
//}
//
//
////    var client = AsynchronousSocketChannel.open();
////     var hostAddress =  InetSocketAddress("localhost", 2004)
////    var f  = client.connect(hostAddress);
////    f.get()
////
////    fun readMessage(message: String?): String {
////        val byteMsg: ByteArray = message?.toByteArray() ?: ByteArray(1024)
////        val buffer = ByteBuffer.wrap(byteMsg)
////        val writeResult: Future<Int> = client.write(buffer)
////
////        // do some computation
////        writeResult.get()
////        buffer.flip()
////        val readResult: Future<Int> = client.read(buffer)
////
////        // do some computation
////        readResult.get()
////        val echo = String(buffer.array()).trim { it <= ' ' }
////        buffer.clear()
////        return echo
////    }
////
////    readMessage()
//
//
//
//
//
//
//
