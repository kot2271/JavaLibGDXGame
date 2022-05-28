package game.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameStarter extends ApplicationAdapter {
  SpriteBatch batch;
  private Panzer me;
  private final List<Panzer> enemies = new ArrayList<>();

  private final KeyboardAdapter inputProcessor;
  private MessageSender messageSender;

    public GameStarter(InputState inputState) {
        this.inputProcessor = new KeyboardAdapter(inputState);
    }

    @Override
  public void create() {
    Gdx.input.setInputProcessor(inputProcessor);
    batch = new SpriteBatch();
    me = new Panzer(100, 200);

    List<Panzer> newEnemies =
        IntStream.range(0, 10)
            .mapToObj(
                i -> {
                  int x = MathUtils.random(Gdx.graphics.getWidth());
                  int y = MathUtils.random(Gdx.graphics.getHeight());
                  return new Panzer(x, y, "Panzer_enemy.png");
                })
            .collect(Collectors.toList());

    enemies.addAll(newEnemies);
  }

  @Override
  public void render() {
    me.moveTo(inputProcessor.getDirection());
    me.rotateTo(inputProcessor.getMousePosition());

    ScreenUtils.clear(1, 1, 1, 1);
    batch.begin();
    me.render(batch);
    enemies.forEach(
        enemy -> {
          enemy.render(batch);
          enemy.rotateTo(me.getPosition());
        });
    batch.end();
  }

  @Override
  public void dispose() {
    batch.dispose();
    me.dispose();
  }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void handleTimer() {
        if (inputProcessor != null){
            InputState playerState = inputProcessor.updateAndGetInputState(me.getOrigin());
            messageSender.sendMessage(playerState);
        }
    }
}
