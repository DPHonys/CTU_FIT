package cz.cvut.fit.honysdan.notification;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

@CssImport("./styles/shared-styles.css")
public class ErrorNotificationButton {
    public ErrorNotificationButton(String text) {
        Button closeButton = new Button("Close");
        Span testText = new Span(text);
        testText.addClassName("Notification-span");
        Notification notification = new Notification(testText, closeButton);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        closeButton.addClickListener(event -> notification.close());
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }
}
