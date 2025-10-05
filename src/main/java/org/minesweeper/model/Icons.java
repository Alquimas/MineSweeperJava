package org.minesweeper.model;

import javax.swing.*;
import java.util.Objects;

public class Icons {
    public static final ImageIcon FLAG_ICON = new ImageIcon(
            Objects.requireNonNull(Icons.class.getResource("/images/flag.png"))
    );
}
