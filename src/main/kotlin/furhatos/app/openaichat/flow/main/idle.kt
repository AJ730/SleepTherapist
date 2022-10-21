package furhatos.app.openaichat.flow

import furhatos.app.openaichat.setting.activate
import furhatos.app.openaichat.setting.hostPersona

import furhatos.flow.kotlin.*


val Idle : State = state {
    onEntry {
        activate(hostPersona)
        furhat.attendNobody()
    }

    onUserEnter {
        furhat.attend(it)
        goto(Greeting)
    }

    

}





