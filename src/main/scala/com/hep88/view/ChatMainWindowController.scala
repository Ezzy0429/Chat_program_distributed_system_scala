package com.hep88.view
import akka.actor.typed.ActorRef
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, ListView, TextField}
import com.hep88._
import scalafx.collections.ObservableBuffer
import scalafx.Includes._

@sfxml
class ChatMainWindowController() {

    def handleJoin(action: ActionEvent) = {
        val okClicked = ChatClientApp.showEnterNameWindowOverview()
    }

    def handleExitProgram(action: ActionEvent): Unit = {

      val okClicked = ChatClientApp.stage.close()

      ChatClientApp.mainSystem.terminate
    }
}