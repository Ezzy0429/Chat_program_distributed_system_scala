package com.hep88.view

import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.event.ActionEvent
import com.hep88._
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableHashSet
import scalafx.scene.control.{Label, ListView, TextField, Alert}
import scalafx.scene.control.{TextField, Alert}
import akka.actor.typed.ActorRef

@sfxml
class GroupChatController(

    private val listMember: ListView[User],
    private val listMessage: ListView[String],
    private val txtMessage: TextField){

    var chatClientRef: Option[ActorRef[ChatClient.Command]] = None
    val receivedText: ObservableBuffer[String] =  new ObservableBuffer[String]()

    listMessage.items = receivedText

    def handleSend(action: ActionEvent): Unit = {

        if (txtMessage.text.value != "") {

        // ! means fire-and-forget, send a message asynchronously and return immediately
        // ? means send a message asyncrhonously and return a Future
        chatClientRef map (_ ! ChatClient.SendMessage(txtMessage.text()))
        println(txtMessage.text())
        }

        else {
            // TODO: remove if later
            if (txtMessage.text.value == ""){
                val alert = new Alert(Alert.AlertType.Error) {
                        initOwner(ChatClientApp.stage)
                        title = "Input Error"
                        headerText = "No Message Entered."
                        contentText = "Please Enter a Message!"
                }.showAndWait()
            }
        }
    }

    def updateList(user: Iterable[User]): Unit = {
        listMember.items = new ObservableBuffer[User]() ++= user
    }

    def addText(text: String): Unit = {
        receivedText += text
    }

    def handleLeaveGroup(action: ActionEvent) = {

        val okClicked = ChatClientApp.showMainScreenOverview()
        chatClientRef map (_ ! ChatClient.Leave)
    }
}