package org.example;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@DisplayName("Main Tests")
class MainTest {

    @Start
    private void start(Stage stage) {
        stage.setScene(new Scene(new Pane(), 200, 120));
        stage.show();
    }

    @Test
    @DisplayName("Main constructor is private")
    void testPrivateConstructor() throws Exception {
        Constructor<Main> ctor = Main.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);
        assertDoesNotThrow(() -> ctor.newInstance((Object[]) null));
    }

    @Test
    @DisplayName("Main delegates to ShoppingCartApp.main (no Mockito)")
    void testMainDelegationPath() {
        // Main.main -> ShoppingCartApp.main -> Application.launch(...)
        // Since FX runtime is already initialized, relaunch throws IllegalStateException.
        assertThrows(IllegalStateException.class, () -> Main.main(new String[0]));
    }
}
