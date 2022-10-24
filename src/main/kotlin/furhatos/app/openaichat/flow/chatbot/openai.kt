package furhatos.app.openaichat.flow.chatbot

import com.theokanning.openai.OpenAiService
import com.theokanning.openai.completion.CompletionRequest
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat
import kotlinx.coroutines.Job


/** API Key to GPT3 language model. Get access to the API and genereate your key from: https://openai.com/api/ **/
val serviceKey = "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"

class OpenAIChatbot(val description: String, val userName: String, val agentName: String) {

    val service = OpenAiService(serviceKey)
    var callCount = 0
    // Read more about these settings: https://beta.openai.com/docs/introduction
    var temperature =
        0.8 // Higher values means the model will take more risks. Try 0.9 for more creative applications, and 0 (argmax sampling) for ones with a well-defined answer.
    var maxTokens =
        500 // Length of output generated. 1 token is on average ~4 characters or 0.75 words for English text
    var topP =
        0.9 // 1.0 is default. An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.
    var frequencyPenalty =
        1.5 // Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim.
    var presencePenalty =
        0.9 // Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model's likelihood to talk about new topics.
    var emotion = ""

    private var previousEmotion = ""
    fun getNextResponse(client: io.socket.client.Socket, msg: Boolean = false): String {
        /** The prompt for the chatbot includes a context of ten "lines" of dialogue. **/


        var history = Furhat.dialogHistory.all.takeLast(60).mapNotNull {
            when (it) {
                is DialogHistory.ResponseItem -> {
                    "$userName: ${it.response.text}"
                }
                is DialogHistory.UtteranceItem -> {
                    "$agentName: ${it.toText()}"
                }
                else -> null
            }
        }.joinToString(separator = "\n")



        if(callCount %6 == 0) {
            client.emit("request", true)
            client.on("emotion") { a ->

                if (a[0] != null) {
                    val data = a[0] as String
                    emotion = data;
//                print("EMOTION-----------$emotion")
                }
            }

            callCount++;
        }

        if (emotion != "" && !emotion.equals(previousEmotion)) {

            if(emotion == "Happy"){
                history = "$history. I am smiling."
            }
            else if(emotion == "Fearful"){
                history = "$history.  I am tired."
            }

            else if(emotion == "Angry"){
                history = "$history. I am tired."
            }

            else{
                history = "$history. I am  $emotion."
            }

            previousEmotion = emotion
            emotion = ""
        }

        if (msg) {
            history = "$history $userName: okay"
        }


        val prompt = "$description\n\n$history\n$agentName:"
        println("-----")
        println(prompt)
        println("-----")
        val completionRequest = CompletionRequest.builder()
            .temperature(temperature)
            .maxTokens(maxTokens)
            .topP(topP)
            .frequencyPenalty(frequencyPenalty)
            .presencePenalty(presencePenalty)
            .prompt(prompt)
            .stop(listOf("\n", " $userName:", " $agentName:"))
            .echo(true)
            .build();

        try {
            val completion = service.createCompletion("davinci", completionRequest).getChoices().first().text
            val response = completion.drop(prompt.length).trim()
            return response
        } catch (e: Exception) {
            print(e)
            println("Problem with connection to OpenAI")
        }


        return "I am not sure what to say"
    }


//    suspend fun generateUniqueID(client: SocketChannel): String =
//        withContext(Dispatchers.IO) {
//            var buffer = ByteBuffer.allocate(2048);
//            client.read(buffer)
//            var msg = ""
//            if(buffer.remaining() != -1) {
//                val curr = String(buffer.array(), StandardCharsets.UTF_8)
//                if(curr.matches("^[a-zA-Z]*$".toRegex())){
//                    msg = curr;
//                }
//            }
//
//            return@withContext msg
//    }

}




