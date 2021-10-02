package com.hep88
import akka.cluster.typed._
import akka.{ actor => classic }
import akka.discovery.{Discovery,Lookup, ServiceDiscovery}
import akka.discovery.ServiceDiscovery.Resolved
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.adapter._
import com.typesafe.config.ConfigFactory
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import scalafx.Includes._
import scala.concurrent.Future
import scala.concurrent.duration._
import javafx.{scene => jfxs}
import scalafx.stage.{Stage, Modality}
import com.hep88.view._
import java.io.File;

import scalafx.scene.image.{Image, ImageView}

object ChatClientApp extends JFXApp {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val config = ConfigFactory.load()
  val mainSystem = akka.actor.ActorSystem("HelloSystem", MyConfiguration.askMyConfig().withFallback(config))
  val greeterMain: ActorSystem[Nothing] = mainSystem.toTyped

  val cluster = Cluster(greeterMain)
  val discovery: ServiceDiscovery = Discovery(mainSystem).discovery
  val userRef = mainSystem.spawn(ChatClient(), "ChatClient")

 def joinPublicSeedNode(): Unit = {

    val lookup: Future[Resolved] =
    discovery.lookup(Lookup("wm.hep88.com").withPortName("hellosystem").withProtocol("tcp"), 1.second)

    lookup.foreach (x => {
        val result = x.addresses
        result map { x =>
            val address = akka.actor.Address("akka", "HelloSystem", x.host, x.port.get)
            cluster.manager ! JoinSeedNodes(List(address))
        }
    })
 }

 def joinLocalSeedNode(): Unit = {
    val address = akka.actor.Address("akka", "HelloSystem", MyConfiguration.localAddress.get.getHostAddress, 2222)
    cluster.manager ! JoinSeedNodes(List(address))
 }

  joinLocalSeedNode()

  var gchatControl: Option[GroupChatController#Controller] = None

  // transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResource("view/RootLayout.fxml")

  // initialize the loader object.
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)

  // Load root layout from fxml file.
  loader.load();

  val border: scalafx.scene.layout.BorderPane = loader.getRoot[javafx.scene.layout.BorderPane]()

  // initialize stage
  stage = new PrimaryStage {
    title = "Group Chat App"
    
    icons += new Image(getClass.getResourceAsStream("/images/mokey1.png"))

    scene = new Scene {
      root = loader.getRoot[jfxs.layout.BorderPane]

      val css_file = new File("app.css")
      stylesheets = Array(css_file.toURI().toURL().toString())
    }
  }

  def showMainScreenOverview() = {

    val resource = getClass.getResource("view/ChatMainWindow.fxml")

    // load fxml file
    val loader = new FXMLLoader(resource, NoDependencyResolver)

    loader.load();

    val border = loader.getRoot[jfxs.layout.AnchorPane]

    // get controller
    val control = loader.getController[com.hep88.view.ChatMainWindowController#Controller]()
    this.border.setCenter(border)
  }

  def showGroupChatOverview() = {

    val resource = getClass.getResource("view/GroupChat.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)

    loader.load()

    val border = loader.getRoot[jfxs.layout.AnchorPane]
    val control = loader.getController[GroupChatController#Controller]

    gchatControl = Some(control)

    control.chatClientRef = Option(userRef)
    this.border.setCenter(border)
  }

  def showEnterNameWindowOverview() = {

    val resource = getClass.getResource("view/EnterNameWindow.fxml")

    // load fxml file
    val loader = new FXMLLoader(resource, NoDependencyResolver)

    loader.load();

    val border = loader.getRoot[jfxs.layout.AnchorPane]

    // get controller
    val control = loader.getController[com.hep88.view.EnterNameWindowController#Controller]()

    this.border.setCenter(border)

    control.chatClientRef = Option(userRef)
  }

  stage.onCloseRequest = handle( {
    mainSystem.terminate
  })

  //--------------------------------------------------
  showMainScreenOverview()
}