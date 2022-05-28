package game.gdx.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import game.gdx.server.actors.ActorPanzer;
import game.gdx.server.ws.WebSocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

@Component
public class GameLoop extends ApplicationAdapter {
  private static final float FRAME_RATE = 1 / 2f;
  private final WebSocketHandler socketHandler;
  private final Json json;
  private float lastRender = 0;
  private final ObjectMap<String, ActorPanzer> panzers = new ObjectMap<>();
  private final ForkJoinPool pool = ForkJoinPool.commonPool();

  public GameLoop(WebSocketHandler socketHandler, Json json) {
    this.socketHandler = socketHandler;
    this.json = json;
  }

  @Override
  public void create() {
    socketHandler.setConnectListener(session -> {
      ActorPanzer panzer = new ActorPanzer();
      panzer.setId(session.getId());
      panzers.put(session.getId(), panzer);
      try {
        session.getNativeSession().getBasicRemote().sendText(session.getId());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    socketHandler.setDisconnectListener(session -> panzers.remove(session.getId()));
    socketHandler.setMessageListener((session, message) ->
            pool.execute(() -> {
      String type = message.getString("type");
      if ("state".equals(type)) {
        ActorPanzer panzer = panzers.get(session.getId());
        panzer.setLeftPressed(message.getBoolean("leftPressed"));
        panzer.setRightPressed(message.getBoolean("rightPressed"));
        panzer.setUpPressed(message.getBoolean("upPressed"));
        panzer.setDownPressed(message.getBoolean("downPressed"));
        panzer.setAngle(message.getFloat("angle"));
      } else {
        throw new RuntimeException("Unknown WS object type " + type);
      }
    }));
  }

  @Override
  public void render() {
    lastRender += Gdx.graphics.getDeltaTime();
    if (lastRender >= FRAME_RATE) {
      for (ObjectMap.Entry<String, ActorPanzer> panzerEntry : panzers) {
        ActorPanzer panzer = panzerEntry.value;
        panzer.act(lastRender);
      }

      lastRender = 0;

      pool.execute(() -> {
        String stateJson = json.toJson(panzers);
        for (StandardWebSocketSession session : socketHandler.getSessions()) {
          try {
            session.getNativeSession().getBasicRemote().sendText(stateJson);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }
}
