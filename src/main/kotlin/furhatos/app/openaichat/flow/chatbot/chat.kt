package furhatos.app.openaichat.flow.chatbot

import furhatos.app.openaichat.flow.*
import furhatos.app.openaichat.flow.chatbot.how_are_you.*
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
        furhat.ask(phrases.howAreYou, endSil = 1000, maxSpeech = 30000)
    }



    onResponse<PositiveReactionIntent>(partial = listOf(HowareYouIntent())){
        val positiveWord: String? =
            it.intent.positiveExpressionEntity?.text // Check for what word the person used in the intent

        print(it.secondaryIntent.getIntentCandidate().intentName)
        if(it.secondaryIntent!=null && it.secondaryIntent.getIntentCandidate().intentName.equals("furhatos.app.openaichat.flow.chatbot.how_are_you.HowareYouIntent")){
            furhat.say(phrases.userFeelsGoodHowAreYou(positiveWord))
        }
        else {
            furhat.say(phrases.gladYouFeelGood(positiveWord))
            furhat.gesture(Gestures.BigSmile)
        }

        goto(Name)
    }



    onResponse<NegativeExpressionEntity>(partial = listOf(HowareYouIntent())) {

        val negativeWord: String? =
            it.intent.text
        furhat.gesture(Gestures.ExpressSad)
        if(it.secondaryIntent!=null && it.secondaryIntent.getIntentCandidate().intentName.equals("furhatos.app.openaichat.flow.chatbot.how_are_you.HowareYouIntent")){
            furhat.say(phrases.userFeelsBadHowAreYou(negativeWord))
        }
        else{
            furhat.say(phrases.wellFeelingsAreComplex)

        }
        delay(400)
        goto(Name)
    }

    onResponse<TiredExpressionEntity> {
        furhat.say(phrases.wellFeelingsAreComplex)
        furhat.gesture(Gestures.ExpressSad)
        delay(400)
        goto(Name)
    }



    onReentry {
        furhat.listen(15000)
    }



    onResponse("can we stop", "goodbye", "I need to go", "I am done", "You ruined my day","Stop") {
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
        furhat.ask("What is your name?", endSil = 1000, maxSpeech = 30000)
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
        furhat.ask("How old are you?", endSil = 1000, maxSpeech = 30000)
    }

    onResponse() {
        goto(sleep)
    }
}

val sleep: State = state(Parent) {

    onReentry {
        furhat.listen(10000, 500)
    }


    onEntry() {
        furhat.ask("Tell me a bit about yourself?", endSil = 20000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 200) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random (
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
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
