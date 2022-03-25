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
import cz.cvut.fit.honysdan.entity.TrainerDTO;

public class TrainerForm extends FormLayout {

    IntegerField id = new IntegerField("Id");
    TextField name = new TextField("Name");
    IntegerField number = new IntegerField ("Number");
    TextField address = new TextField("Address");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Binder<TrainerDTO> binder = new Binder<>(TrainerDTO.class);
    private TrainerDTO trainer;

    public TrainerForm() {
        addClassName("trainer-form");
        formSetUp();
        bindDataToFields();

        add(id, name, number, address, buttons());
    }

//----------------------------------------------------------------------------------------------------------------------
// FORM SETUP
//----------------------------------------------------------------------------------------------------------------------

    private void formSetUp() {
        id.setReadOnly(true);

        name.setRequired(true);

        name.setClearButtonVisible(true);
        number.setClearButtonVisible(true);
        address.setClearButtonVisible(true);
    }

    private void bindDataToFields() {
        binder.forField(id).withNullRepresentation(null)
                .bind(TrainerDTO::getId, TrainerDTO::setId);
        binder.forField(name)
                .withValidator(name -> name.trim().length() != 0, "Name can't be empty")
                .withValidator(new StringLengthValidator("Max length is 30 characters", 0, 30))
                .bind(TrainerDTO::getName, TrainerDTO::setName);
        binder.forField(number)
                .withNullRepresentation(null)
                .bind(TrainerDTO::getNumber, TrainerDTO::setNumber);
        binder.forField(address)
                .withValidator(new StringLengthValidator("Max length is 30 characters", 0, 30))
                .bind(TrainerDTO::getAddress, TrainerDTO::setAddress);
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
        delete.addClickListener(click -> fireEvent(new TrainerForm.DeleteEvent(this, trainer)));
        close.addClickListener(click -> fireEvent(new TrainerForm.CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return h;
    }

    private void validateAndSave() {
        try {
            binder.writeBean(trainer);
            fireEvent(new TrainerForm.SaveEvent(this, trainer));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// TRAINER FROM GRID
//----------------------------------------------------------------------------------------------------------------------

    public void setTrainer(TrainerDTO trainer) {
        this.trainer = trainer;
        binder.readBean(trainer);
    }

//----------------------------------------------------------------------------------------------------------------------
// EVENTS
//----------------------------------------------------------------------------------------------------------------------

    public abstract static class TrainerFormEvent extends ComponentEvent<TrainerForm> {
        private final TrainerDTO trainer;

        protected TrainerFormEvent(TrainerForm source, TrainerDTO trainer) {
            super(source, false);
            this.trainer = trainer;
        }

        public TrainerDTO getTrainer() {
            return trainer;
        }
    }

    public static class SaveEvent extends TrainerFormEvent {
        SaveEvent(TrainerForm source, TrainerDTO trainer) {
            super(source, trainer);
        }
    }

    public static class DeleteEvent extends TrainerFormEvent {
        DeleteEvent(TrainerForm source, TrainerDTO trainer) {
            super(source, trainer);
        }

    }

    public static class CloseEvent extends TrainerFormEvent {
        CloseEvent(TrainerForm source) {
            super(source, null);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
