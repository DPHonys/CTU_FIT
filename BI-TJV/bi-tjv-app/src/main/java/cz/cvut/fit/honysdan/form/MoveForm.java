package cz.cvut.fit.honysdan.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import cz.cvut.fit.honysdan.entity.MoveDTO;

public class MoveForm extends FormLayout {

    IntegerField id = new IntegerField("Id");
    TextField name = new TextField("Name");
    TextField description = new TextField ("Description");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Binder<MoveDTO> binder = new Binder<>(MoveDTO.class);
    private MoveDTO move;

    public MoveForm() {
        addClassName("move-form");
        formSetUp();
        bindDataToFields();

        add(id, name, description, buttons());
    }

//----------------------------------------------------------------------------------------------------------------------
// FORM SETUP
//----------------------------------------------------------------------------------------------------------------------

    private void formSetUp() {
        id.setReadOnly(true);

        name.setRequired(true);

        name.setClearButtonVisible(true);
        description.setClearButtonVisible(true);
    }

    private void bindDataToFields() {
        binder.forField(id).withNullRepresentation(null)
                .bind(MoveDTO::getId, MoveDTO::setId);
        binder.forField(name)
                .withValidator(name -> name.trim().length() != 0, "Name can't be empty")
                .withValidator(new StringLengthValidator("Max length is 30 characters", 0, 30))
                .bind(MoveDTO::getName, MoveDTO::setName);
        binder.forField(description)
                .withValidator(new StringLengthValidator("Max length is 200 characters", 0, 200))
                .bind(MoveDTO::getDescription, MoveDTO::setDescription);
    }

    private Component buttons() {
        HorizontalLayout h = new HorizontalLayout(save, delete, close);
        h.addClassName("buttons");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new MoveForm.DeleteEvent(this, move)));
        close.addClickListener(click -> fireEvent(new MoveForm.CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return h;
    }

    private void validateAndSave() {
        try {
            binder.writeBean(move);
            fireEvent(new MoveForm.SaveEvent(this, move));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// MOVE FROM GRID
//----------------------------------------------------------------------------------------------------------------------

    public void setMove(MoveDTO move) {
        this.move = move;
        binder.readBean(move);
    }

//----------------------------------------------------------------------------------------------------------------------
// EVENTS
//----------------------------------------------------------------------------------------------------------------------

    public abstract static class MoveFormEvent extends ComponentEvent<MoveForm> {
        private final MoveDTO move;

        protected MoveFormEvent(MoveForm source, MoveDTO move) {
            super(source, false);
            this.move = move;
        }

        public MoveDTO getMove() {
            return move;
        }
    }

    public static class SaveEvent extends MoveForm.MoveFormEvent {
        SaveEvent(MoveForm source, MoveDTO move) {
            super(source, move);
        }
    }

    public static class DeleteEvent extends MoveForm.MoveFormEvent {
        DeleteEvent(MoveForm source, MoveDTO move) {
            super(source, move);
        }

    }

    public static class CloseEvent extends MoveForm.MoveFormEvent {
        CloseEvent(MoveForm source) {
            super(source, null);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
