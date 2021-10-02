package com.hep88.view
import akka.actor.typed.ActorRef
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, ListView, TextField, Alert}
import com.hep88._
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.stage.Stage
// import scalafx.scene.control.Alert

@sfxml
class RootLayoutController() {

  //function to close the chat system
  def handleCloseMenuItem(action: ActionEvent) = {
    val okClicked = ChatClientApp.stage.close()
        ChatClientApp.mainSystem.terminate
  }

  //function to bring users back to the Home interface
  def handleHomeMenuItem(action: ActionEvent) = {
    val okClicked = ChatClientApp.showMainScreenOverview()
  }

  def handleInfo(action: ActionEvent) = { 

    var student1 = "Ahsan Shahid Minhas - 18050096\n"
    var student2 = "Chan Weng Keit - 18088492\n"
    var student3 = "Eh Zheng Yang - 18074013\n"
    var student4 = "Leong Wen Hao - 17003906\n"
    var student5 = "Tan Eson - 17071143"

    val showInfo = new Alert(Alert.AlertType.Information){

      initOwner(ChatClientApp.stage)
      title = "Information Dialog"
      headerText = "Chat program by Group Spartans"
      contentText = student1 + student2 + student3 + student4 + student5
    }.showAndWait()
  }
}