package game.gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Panzer {
  private static final float SIZE = 64;
  private static final float HALF_SIZE = SIZE / 2;

  private final Vector2 position = new Vector2();
  private final Vector2 angle = new Vector2();
  private final Vector2 origin = new Vector2();

  private final Texture texture;
  private final TextureRegion textureRegion;

  public Panzer(float x, float y) {
    this(x, y, "Panzer_me.png");
  }

  public Panzer(float x, float y, String textureName) {
    texture = new Texture(textureName);
    textureRegion = new TextureRegion(texture);
    position.set(x, y);
    origin.set(position).add(HALF_SIZE, HALF_SIZE);
  }

  public void render(Batch batch) {
    batch.draw(
        textureRegion,
        position.x,
        position.y,
            HALF_SIZE,
            HALF_SIZE,
            SIZE,
            SIZE,
        1,
        1,
        angle.angleDeg() - 90);
  }

  public void dispose() {
    texture.dispose();
  }

  public void moveTo(Vector2 direction) {
    position.add(direction);
    origin.set(position).add(HALF_SIZE, HALF_SIZE);
  }

  public void rotateTo(Vector2 mousePosition) {
    angle.set(mousePosition).sub(origin);
  }

  public Vector2 getPosition() {
    return position;
  }

  public Vector2 getOrigin() {
    return origin;
  }
}
