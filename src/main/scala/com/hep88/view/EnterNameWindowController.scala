package com.hep88.view
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, ListView, TextField}
import scalafx.scene.control.{TextField, Alert}
import akka.actor.typed.ActorRef
import com.hep88._

@sfxml
class EnterNameWindowController(
    private val nameField: TextField
){
    var chatClientRef: Option[ActorRef[ChatClient.Command]] = None

    def handleJoin(action : ActionEvent){

        if(nameField.text.value != "") {

          //map on the chatClientRef, _ is the ActorRef object and tells a message to the ChatClient that
          //it can start to join with name in txtName.text
          //txtName.text() will return a string of the textField
          chatClientRef map (_ ! ChatClient.StartJoin(nameField.text()))

          println(nameField.text())

          val okClicked = ChatClientApp.showGroupChatOverview()

        } else {

            // checks if namefield is "" or null
            if (nameField.text.value == ""){
                val alert = new Alert(Alert.AlertType.Error) {
                    initOwner(ChatClientApp.stage)
                    title = "Input Error"
                    headerText = "No Name Entered."
                    contentText = "Please Enter a Name!"
                }.showAndWait()
            }
        }
    }

    def handleBack(action:ActionEvent){
        val okClicked = ChatClientApp.showMainScreenOverview()
    }
}