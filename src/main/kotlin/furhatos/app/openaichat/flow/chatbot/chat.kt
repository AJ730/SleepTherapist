package furhatos.app.openaichat.flow.chatbot

import furhatos.app.openaichat.flow.*
import furhatos.app.openaichat.flow.chatbot.how_are_you.NegativeExpressionEntity
import furhatos.app.openaichat.flow.chatbot.how_are_you.PositiveReactionIntent
import furhatos.app.openaichat.flow.chatbot.how_are_you.phrases
import furhatos.app.openaichat.setting.activate
import furhatos.app.openaichat.setting.hostPersona
import furhatos.autobehavior.setDefaultMicroexpression
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.nlu.NullIntent
import furhatos.nlu.Response
import io.socket.client.Socket
import java.io.*



var msocket: Socket? = null
val MainChat = state(Parent) {



    SocketHandler
    SocketHandler.setSocket()
    SocketHandler.establishConnection()
    msocket = SocketHandler.getSocket()



    onEntry {
        furhat.setDefaultMicroexpression(blinking = true, facialMovements = true, eyeMovements = true)
        activate(hostPersona)
        delay(2000)
        Furhat.dialogHistory.clear()
        furhat.say("Hello, I am ${hostPersona.fullDesc}.")
        furhat.ask(phrases.howAreYou, 8000)
    }


//    onPartialResponse<PositiveReactionIntent> { // Catches a Greeting together with another intent, such as Order
//        // Greet the user and proceed with the order in the same turn
//        raise(it, it.secondaryIntent)
//    }

    onResponse<PositiveReactionIntent> {
        val positiveWord: String? =
            it.intent.positiveExpressionEntity?.text // Check for what word the person used in the intent
        furhat.say(phrases.gladYouFeelGood(positiveWord))
        furhat.gesture(Gestures.BigSmile)
        goto(Name)
    }

    onResponse<NegativeExpressionEntity> {
        furhat.say(phrases.wellFeelingsAreComplex)
        furhat.gesture(Gestures.ExpressSad)
        delay(400)
        goto(Name)
    }


    onReentry {
        furhat.listen(8000)
    }



    onResponse("can we stop", "goodbye", "I need to go", "I am done", "You ruined my day") {
        furhat.say("Okay, goodbye")
        val stream = PrintStream(File("akash"))
        Furhat.dialogHistory.dump(stream)
        delay(2000)
        furhat.say {
            random {
                +"I hope that was fun"
                +"I hope you enjoyed that"
                +"I hope you found that interesting"
            }
        }
        goto(AfterChat)
    }

    onNoResponse {
        reentry()
    }
}


val Name: State = state(Parent) {

    onReentry {
        furhat.listen(8000)
    }

    onEntry() {
        furhat.say("Lets start!")
        furhat.say("First I will ask you a couple of questions")
        furhat.ask("What is your name?", 8000)
    }

    onResponse() {
        goto(Age)
    }
}


val Age: State = state(Parent) {

    onReentry {
        furhat.listen(8000)
    }


    onEntry() {
        furhat.ask("How old are you?", 5000)
    }

    onResponse() {
        goto(sleep)
    }
}

val sleep: State = state(Parent) {

    onReentry {
        furhat.listen(8000)
    }


    onEntry() {
        furhat.ask("Tell me a bit about yourself?", 20000)
    }


    onResponse {
        furhat.gesture(GazeAversion(2.0))
        val response = call {
            msocket?.let { it1 -> hostPersona.chatbot.getNextResponse(client = it1) }
        } as String

        var emotion = Gestures.Blink
        if (response.endsWith(":)")) {
            response.dropLast(2);
            emotion = Gestures.BigSmile
        }

        if (response.endsWith(":(")) {
            response.dropLast(2);
            emotion = Gestures.ExpressSad
        }

        furhat.gesture(emotion)
        furhat.say(response)
        reentry()
    }

    onNoResponse {
        val response = call {
            msocket?.let { it1 -> hostPersona.chatbot.getNextResponse(client = it1, msg = true) }
        } as String

        furhat.say(response)
        reentry()
    }

}


val AfterChat: State = state(Parent) {

    onEntry {
        furhat.ask("It was fun getting to know you. I hope this therapy session felt more like a conversation. Hope to see you again.")
        SocketHandler.closeConnection()
        goto(Idle)
    }
//
//    onPartialResponse<Yes> {
//        raise(it.secondaryIntent)
//    }

//    onResponse<Yes> {
//        goto(ChoosePersona())
//    }

//    onResponse<No> {
//        furhat.say("Okay, goodbye then")
//        goto(Idle)
//    }

//    for (persona in personas) {
//        onResponse(persona.intent) {
//            furhat.say("Okay, I will let you talk to ${persona.name}")
//            currentPersona = persona
//            goto(MainChat)
//        }
//    }
}
