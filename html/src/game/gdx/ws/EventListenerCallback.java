package game.gdx.ws;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface EventListenerCallback {
    void callEvent(WsEvent event);
}
