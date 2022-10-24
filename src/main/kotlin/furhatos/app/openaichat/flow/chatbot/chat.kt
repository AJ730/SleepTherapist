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


    var badCount = 0

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
        if(badCount >= 2){
            goto(openAi)
        }

        badCount++
        reentry()
    }
}


val Name: State = state(Parent) {

    var badCount = 0

    onReentry {
        furhat.listen( endSil= 2000)
    }

    onEntry() {
        furhat.say("Lets start!")
        furhat.say("First I will ask you a couple of questions")
        furhat.ask("What is your name?", endSil = 1000, maxSpeech = 30000)
    }

    onResponse() {
        goto(Age)
    }

    onNoResponse {
        if(badCount >= 2){
            goto(openAi)
        }

        badCount++
        reentry()
    }
}


val Age: State = state(Parent) {
    var badCount = 0

    onReentry {
        furhat.listen( endSil= 2000)
    }


    onEntry() {
        furhat.ask("How old are you?", endSil = 1000, maxSpeech = 30000)
    }

    onResponse() {
        goto(yourself)
    }

    onNoResponse {
        if(badCount >= 2){
            goto(openAi)
        }

        badCount++
        reentry()
    }
}

val yourself: State = state(Parent) {

    onReentry {
        furhat.listen( endSil= 2000)
    }


    onEntry() {
        furhat.ask("Tell me a bit about yourself?", endSil = 10000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 200) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }

    onResponse {
        goto(sleptLastNight)
    }

    onNoResponse {
        reentry()
    }
}

//    onTime(delay=1000*60*1) {
//        furhat.say("Tell me a little bit about your sleep?")
//        furhat.say("What time do you usually go to bed?")
//        reentry()
//    }

val sleptLastNight: State = state(Parent){
    onReentry {
        furhat.listen( endSil= 1000)
    }

    onEntry() {
        furhat.ask("It seems to me that you are a student. If i may ask, did you study until late yesterday? " +
                "More importantly, Are you getting enough sleep?", endSil = 5000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 200) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }
    onResponse {
        goto(sleeptime)
    }

    onNoResponse {
        reentry()
    }
}


val sleeptime: State = state(Parent){
    onReentry {
        furhat.listen( endSil= 1000)
    }

    onEntry() {
        furhat.ask("What time did you go to bed yesterday?", endSil = 5000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 200) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }
    onResponse {
        goto(Sports)
    }

    onNoResponse {
        reentry()
    }
}


val Sports: State = state(Parent){
    onReentry {
        furhat.listen( endSil= 2000)
    }

    onEntry() {
        furhat.ask("Tell me about your hobbies, do you play any sports?", endSil = 2000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 6000) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }
    onResponse {

        goto(Therapy)
    }
    onNoResponse {
        reentry()
    }

}


val Therapy: State = state(Parent){
    onReentry {
        furhat.listen( endSil= 1000)
    }

    onEntry() {
        furhat.gesture(Gestures.Thoughtful)
        furhat.say(" sports help a lot with sleep.")
        furhat.say("Sleeping helps a lot with studies. Research shows that sleep helps improve cognitive capabilities which means you will be able to study better. ")
        furhat.ask("can you tell about how fast you go into sleep?", endSil = 2000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 2000) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }
    onResponse {
        goto(remsleep)
    }

    onNoResponse {
        reentry()
    }
}

val remsleep: State = state(Parent){
    onReentry {
        furhat.listen( endSil= 1000)
    }

    onEntry() {
        furhat.ask("Do you know what REM sleep is? It is when you enter the stage of rapid eye movement and it is when your whole body rests. Do you think you might have entered rem sleep before?", endSil = 2000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 1000) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }
    onResponse {
        goto(sleepParalyis)
    }

    onNoResponse {
        reentry()
    }
}

val sleepParalyis: State = state(Parent){
    onReentry {
        furhat.listen( endSil= 1000)
    }

    onEntry() {
        furhat.ask("What do you know about sleep paralysis? Have you ever had such an experience?", endSil = 3000)
    }

    onInterimResponse(endSil = 5000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 2000) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }
    onResponse {
        goto(coffee)
    }

    onNoResponse {
        reentry()
    }
}

val coffee: State = state(Parent){
    onReentry {
        furhat.listen( endSil= 1000)
    }

    onEntry() {
        furhat.ask("As a student I will be surprised if you don't drink a lot of coffee, or maybe tea or maybe you are a sugar junkie. But do you consume a lot before sleep?", endSil = 2000)
    }

    onInterimResponse(endSil = 2000) {
        furhat.gesture(Gestures.Nod)
    }

    onInterimResponse(endSil = 2000) {
        // We give some feedback to the user, "okay" or a nod gesture.
        random(
            // Gestures are async per default, so no need to set the flag
            {
                furhat.gesture(Gestures.Oh)
                furhat.gesture(Gestures.BrowRaise(2.0, 200.0))
            }
        )
    }
    onResponse {
        furhat.say("Alright, now I know a bit about you. In this session you can ask me any questions and lets not make this formal. " +
                "But a friendly conversation, at the end of this session i hope to have informed you a bit about sleep habits and hopefully cheer you up a bit")
        furhat.gesture(Gestures.Smile)

        goto(openAi)
    }

    onNoResponse {
        reentry()
    }
}

val openAi: State = state(Parent){

    onEntry{
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
        furhat.ask(response)
    }

    onResponse("can we stop", "goodbye", "I need to go", "I am done","Stop", "I have to go","I am done with you", "Can we do this another time") {
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

    onTime(420000){
        furhat.say("I am sorry to interrupt you, but our session is finished.")
        goto(AfterChat)
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

    onReentry {
        furhat.listen( endSil= 2000)
    }


}


val AfterChat: State = state(Parent) {

    onEntry {
        furhat.ask("It was fun getting to know you. I hope this therapy session felt more like a conversation. Hope to see you again.")
        SocketHandler.closeConnection()
        goto(Idle)
    }

    onResponse(){
        goto(Idle)
    }

    onNoResponse(){
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
