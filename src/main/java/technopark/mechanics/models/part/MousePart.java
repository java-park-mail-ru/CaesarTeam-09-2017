package technopark.mechanics.models.part;

import org.jetbrains.annotations.NotNull;
import technopark.mechanics.models.Coords;
import technopark.mechanics.models.Snap;


public class MousePart implements GamePart {
    @NotNull
    private Coords mouse;


    public MousePart() {
        this.mouse = new Coords(0, 0);
    }

    @NotNull
    public Coords getMouse() {
        return mouse;
    }

    public void setMouse(@NotNull Coords mouse) {
        this.mouse = mouse;
    }

    @Override
    public MouseSnap takeSnap() {
        return new MouseSnap(this);
    }

    public static final class MouseSnap implements Snap<MousePart> {

        private final Coords mouse;

        public MouseSnap(MousePart mouse) {
            this.mouse = mouse.getMouse();
        }

        public Coords getMouse() {
            return mouse;
        }
    }
}
