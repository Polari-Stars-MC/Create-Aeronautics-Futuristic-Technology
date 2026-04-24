package org.polaris2023.caft.compat.emi;

public interface InteractiveEmiWidget {
    boolean caft$mouseReleased(int mouseX, int mouseY, int button);

    boolean caft$mouseDragged(int mouseX, int mouseY, int button, double dragX, double dragY);

    boolean caft$mouseScrolled(int mouseX, int mouseY, double scrollY);

    boolean caft$isInteracting();
}
