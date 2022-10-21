package furhatos.app.openaichat.flow.chatbot.how_are_you

import furhatos.flow.kotlin.Utterance
import furhatos.flow.kotlin.utterance
import furhatos.gestures.Gestures

val phrases = HowAreYouPhrases()

class HowAreYouPhrases {
    val feelGood: String = listOf("good", "pretty good").random()
    val feelGoodUtterance: Utterance
        get() = utterance {
            +"I feel"
            random {
                +"good"
                +"pretty good"
            }
            +Gestures.BigSmile
        }
    val howAreYou: Utterance
        get() = utterance {
            random {
                // use prosody tag for emphasis
                +"How are you today?"
                +"How are you feeling?"
            }
        }


    fun userFeelsGoodHowAreYou(positiveWord: String? = "good"): Utterance {
        Gestures.BigSmile(0.6, 2.0)
        if (positiveWord != null) {
            return utterance {
                random {
                    +"Glad to hear you feel $positiveWord, I am $positiveWord too"
                    +"Nice that you feel $positiveWord,  I am $positiveWord too"
                }
                delay(200)
            }
        } else {
            return utterance {
                random{
                    +"Glad to hear that. I am ok. Thanks for asking"
                    +"Nice to hear that. I am ok. Thanks for asking "
                }
            }
        }
    }


    fun userFeelsBadHowAreYou(negativeWord: String? = "bad"): Utterance {
        Gestures.ExpressSad(0.6, 2.0)
        if (negativeWord != null) {
            return utterance {
                random {
                    +"I am sorry that you feel $negativeWord, I feel good but I hope to make you feel good at the end of this session"
                    +"Sorry that you feel $negativeWord,   I feel good but I hope to make you feel good at the end of this session"
                }
                delay(200)
            }
        } else {
            return utterance {
                random{
                    +"I am sorry that you feel that way. I am ok. Thanks for asking"
                    +"I am sorry that you feel that way. I am ok. Thanks for asking "
                }
            }
        }
    }

    fun gladYouFeelGood(positiveWord: String? = "good"): Utterance {
        if (positiveWord != null) {
            return utterance {
                random {
                    +"Glad to hear you feel $positiveWord"
                    +"Nice that you feel $positiveWord"
                }
                delay(200)
            }
        } else {
            return utterance {
                random{
                    +"Glad to hear that. "
                    +"Nice to hear that. "
                }
            }
        }
    }

    val wellFeelingsAreComplex: Utterance
        get() = utterance {
            +Gestures.ExpressSad(0.6, 2.0)
            random {
                +"I am sorry, I hope our session makes you feel better."
                +"I am sorry, I  hope I can make you feel better."
            }
        }
}

