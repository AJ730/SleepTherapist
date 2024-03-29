package furhatos.app.openaichat.flow

import furhatos.app.openaichat.flow.chatbot.MainChat
import furhatos.app.openaichat.setting.Persona
import furhatos.app.openaichat.setting.hostPersona
import furhatos.app.openaichat.flow.Parent
import furhatos.flow.kotlin.*
import furhatos.records.Location

val Greeting = state(Parent) {

    onEntry {
        furhat.attend(users.userClosestToPosition(Location(0.0, 0.0, 0.5)))
        goto(MainChat)
    }
}

//var currentPersona: Persona = hostPersona

//fun ChoosePersona() = state(Parent) {
//    onEntry {
//        goto(MainChat)
//    }
//    val personasWithAvailableVoice = personas.filter { it.voice.first().isAvailable }
//    val selectedPersonas = personasWithAvailableVoice.take(3)
//
//    fun FlowControlRunner.presentPersonas() {
//        furhat.say("You can choose to speak to one of these characters:")
//        for (persona in selectedPersonas) {
//            //activate(persona)
//            delay(300)
//            furhat.say(persona.fullDesc)
//            delay(300)
//        }
//        //activate(hostPersona)
//        reentry()
//    }
//
//    onEntry {
//        presentPersonas()
//    }

//    onReentry {
//        furhat.ask("Who would you like to talk to?")
//    }
//
//    onResponse("can you present them again", "could you repeat") {
//        presentPersonas()
//    }
//
//    for (persona in personas) {
//        onResponse(persona.intent) {
//            furhat.say("Okay, I will let you talk to ${persona.name}.")
//            furhat.say("When you want to end the conversation, just say goodbye.")
//            currentPersona = persona
//            goto(MainChat)
//        }
//    }
//}