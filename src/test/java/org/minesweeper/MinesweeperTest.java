package org.minesweeper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MinesweeperTest {

    @Test
    void testAddWithOne() {
        Minesweeper app = new Minesweeper();
        assertEquals(6, app.addWithOne(2, 3));
        assertEquals(-2, app.addWithOne(-2, -1));
        assertEquals(1, app.addWithOne(0, 0));
    }
}
