package game.gdx.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.user.client.Timer;
import game.gdx.GameStarter;
import game.gdx.client.dto.InputStateImpl;
import game.gdx.client.ws.EventListenerCallback;
import game.gdx.client.ws.WebSocket;

import java.util.concurrent.atomic.AtomicBoolean;

public class HtmlLauncher extends GwtApplication {

  @Override
  public GwtApplicationConfiguration getConfig() {
    // Resizable application, uses available space in browser
    return new GwtApplicationConfiguration(true);
    // Fixed size application:
    // return new GwtApplicationConfiguration(480, 320);
  }

  private native WebSocket getWebsocket(String url)
      /*-{
           return new WebSocket(url);
      }-*/
      ;

  private native void log(Object object)
      /*-{
              console.log(object);
      }-*/
      ;

  private native String toJson(Object object)
    /*-{
            return JSON.stringify(object);
    }-*/
  ;

  @Override
  public ApplicationListener createApplicationListener() {
    WebSocket client = getWebsocket("ws://localhost:8888/ws");
    AtomicBoolean once = new AtomicBoolean(false);

    GameStarter gameStarter = new GameStarter(new InputStateImpl());
    gameStarter.setMessageSender(message ->
            client.send(toJson(message))
    );

    Timer timer = new Timer() {
      @Override
      public void run() {
        gameStarter.handleTimer();
      }
    };
    timer.scheduleRepeating(1000);

    EventListenerCallback callback =
        event -> {
      if (! once.get()) {
          client.send("Hello");
          once.set(true);
      }
          log(event.getData());
        };

    client.addEventListener("open", callback);
    client.addEventListener("close", callback);
    client.addEventListener("error", callback);
    client.addEventListener("message", callback);

    return gameStarter;
  }
}
