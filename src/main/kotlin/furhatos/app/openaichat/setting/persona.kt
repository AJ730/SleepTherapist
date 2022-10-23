package furhatos.app.openaichat.setting

import furhatos.app.openaichat.flow.chatbot.OpenAIChatbot
import furhatos.flow.kotlin.FlowControlRunner
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.voice.AcapelaVoice
import furhatos.flow.kotlin.voice.PollyNeuralVoice
import furhatos.flow.kotlin.voice.Voice
import furhatos.nlu.SimpleIntent

class Persona(
    val name: String,
    val otherNames: List<String> = listOf(),
    val intro: String = "",
    val desc: String,
    val face: List<String>,
    val mask: String = "adult",
    val voice: List<Voice>
) {
    val fullDesc = "$name, the $desc"

    val intent = SimpleIntent((listOf(name, desc, fullDesc) + otherNames))

    /** The prompt for the openAI language model **/
    val chatbot =
        OpenAIChatbot(
                "Seirra is a well renowned sleep therapist who is an expert on student sleep help, " +
                        "When it comes to sleep issues, there is usually a question of origin: Is a sleep disorder causing mental health symptoms or is your mental health affecting your sleep? Some common disorders that implicate sleep include depression, anxiety, bipolar disorder, substance abuse, OCD, and PTSD,” says Seirra, a professor and clinical psychologist." +
                        " Today," +
                        "Akash has come to therapy session. He is very sleep derpived he does not have a good balance, he needs to learn how to sleep and practice sleep and breathing habits. He is currently" +
                        "studying at TU DElFT and is very stressed about his exams which makes him more anxious about sleep"
            , "Akash", name)
}

fun FlowControlRunner.activate(persona: Persona) {
    for (voice in persona.voice) {
        if (voice.isAvailable) {
            furhat.voice = voice
            break
        }
    }

    for (face in persona.face) {
        if (furhat.faces[persona.mask]?.contains(face)!!) {
            furhat.character = face
            break
        }
    }
}

val hostPersona = Persona(
    name = "Seirra",
    desc = "Sleep Therapist",
    face = listOf("Isabel"),
    intro = "How are you today?",
    voice = listOf(AcapelaVoice("WillSad"), PollyNeuralVoice("Kimberly"))
)

//val personas = listOf(
//    Persona(
//        name = "Seirra",
//        desc = "Sleep Therapist",
//        face = listOf("Isabel"),
//        intro = "How was your day?",
//        voice = listOf(AcapelaVoice("WillSad"), PollyNeuralVoice("Kimberly"))
//    )
//    Persona(
//        name = "Emma",
//        desc = "personal trainer",
//        intro = "How do you think I could help you?",
//        face = listOf("Isabel"),
//        voice = listOf(PollyNeuralVoice("Olivia"))
//    ),
//    Persona(
//        name = "Jerry Seinfeld",
//        desc = "famous comedian",
//        otherNames = listOf("Seinfeld", "Jerry"),
//        intro = "You know, crankiness is at the essence of all comedy.",
//        face = listOf("Marty"),
//        voice = listOf(AcapelaVoice("WillFromAfar"), PollyNeuralVoice("Joey"))
//    ),
//    Persona(
//        name = "James",
//        desc = "guide at the British museum",
//        intro = "What can I help you with?",
//        face = listOf("Samuel"),
//        voice = listOf(PollyNeuralVoice("Brian"))
//    ),
//    Persona(
//        name = "Albert Einstein",
//        otherNames = listOf("Einstein", "Albert"),
//        desc = "famous scientist",
//        intro = "What can I help you with?",
//        face = listOf("James"),
//        voice = listOf(AcapelaVoice("WillOldMan"), PollyNeuralVoice("Brian"))
//    )
//)