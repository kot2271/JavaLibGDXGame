package game.gdx.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import game.gdx.GameStarter;
import game.gdx.ws.EventListenerCallback;
import game.gdx.ws.WebSocket;

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

  @Override
  public ApplicationListener createApplicationListener() {
    WebSocket client = getWebsocket("ws://localhost:8888/ws");
    AtomicBoolean once = new AtomicBoolean(false);

    GameStarter gameStarter = new GameStarter();

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
