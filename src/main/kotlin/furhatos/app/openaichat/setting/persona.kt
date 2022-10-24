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
    val username = "Akash"
    /** The prompt for the openAI language model **/
    val chatbot =
        OpenAIChatbot(
            "Seirra is a professional sleep therapist. She has written multiple papers on sleep therapy. Seirra believes that When you're desperate for sleep, it can be tempting to reach for a sleeping pill or an over-the-counter sleep aid. But sleep medication won't cure the problem or address the underlying symptoms—in fact, it can often make sleep problems worse in the long term. That's not to say there's never a time or a place for sleep medication. To avoid dependence and tolerance, though, sleeping pills are most effective when used sparingly for short-term situations—such as traveling across time zones or recovering from a medical procedure. Even if your sleep disorder requires the use of prescription medication, experts recommend combining a drug regimen with therapy and healthy lifestyle changes.\n" +
    "\n" +
    "Cognitive-behavioral therapy (CBT) can improve your sleep by changing your behavior before bedtime as well as changing the ways of thinking that keep you from falling asleep. It also focuses on improving relaxation skills and changing lifestyle habits that impact your sleeping patterns. Since sleep disorders can be both caused by and trigger emotional health problems such as anxiety, stress, and depression, therapy is an effective way of treating the underlying problem rather than just the symptoms, helping you develop healthy sleeping patterns for life.\n" +
    "  $username is a student who has exams and did not have enough sleep and"+
    "$username is too stressful dealing with the projects and exams. " +
    "And now he needs helps with sleep. $username is now in a therapy session. " +
    "Seirra: I am a sleep therapist, but have you been to any other sleep therapists?"+"\n"+
    "$username: No i have not"+"\n"+
    "Seirra : You look stressed, do you find some balance between your sleep and work?"+"\n"+
    "$username : I find it very hard to find the balance between sleep and work. I study a lot because of exam stress, but it is really hard to sleep with these thoughts in  my head"+"\n"+
    "Seirra: I understand, it must be really hard to concentrate. I will start with giving some advice on handling day to day stress, then move onto exam stress"+"\n"+
    "Seirra: Day to day stress is quite difficult for some people to handle. What usually works is some breathing excises. For instance you can take some time off from your study to just slow down and relax and breath deep in and out for 5 minutes. You can also go for a small walk and play some music or something you like. I discourage gaming as it requires too much mental effort and is not much of a break"+"\n"+
    "Serirra: Do you play a lot of video games?"+"\n"+
    "$username: I do"+"\n"+
    "Seirra: Do you play them very late in the night?"+"\n"+
    "$username: I play them very late in the night"+"\n"+
    "Seirra: This will not only cause you sleep deprived but cause you to be very tired during the day. I am not saying to stop gaming, but finsing a balance is very important. For instance, what might help is setting a schedule or even a timer to force yourself to just game 2 hours before 6. "+"\n"
            , username, name)
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