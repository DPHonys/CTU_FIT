package cz.cvut.fit.honysdan.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import cz.cvut.fit.honysdan.converter.MoveConverter;
import cz.cvut.fit.honysdan.converter.TrainerConverter;
import cz.cvut.fit.honysdan.entity.MoveDTO;
import cz.cvut.fit.honysdan.entity.PokemonDTO;
import cz.cvut.fit.honysdan.entity.TrainerDTO;
import cz.cvut.fit.honysdan.resource.MoveResource;
import cz.cvut.fit.honysdan.resource.TrainerResource;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;

public class PokemonForm extends FormLayout {

    TrainerResource trainerResource;
    MoveResource moveResource;

    IntegerField id = new IntegerField("Id");
    TextField name = new TextField("Name");
    TextField type = new TextField("Type");

    MultiselectComboBox<MoveDTO> movesIds = new MultiselectComboBox<>("Moves");
    ComboBox<TrainerDTO> trainerId = new ComboBox<>("Trainer");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Binder<PokemonDTO> binder = new Binder<>(PokemonDTO.class);
    private PokemonDTO pokemon;

    public PokemonForm(List<TrainerDTO> listTrainers, List<MoveDTO> listMoves,
                       TrainerResource trainerResource, MoveResource moveResource) {

        this.trainerResource = trainerResource;
        this.moveResource = moveResource;

        addClassName("pokemon-form");
        formSetUp(listTrainers, listMoves);
        bindDataToFields();

        add(id, name, type, movesIds, trainerId, buttons());
    }

//----------------------------------------------------------------------------------------------------------------------
// FORM SETUP
//----------------------------------------------------------------------------------------------------------------------

    private void formSetUp(List<TrainerDTO> listTrainers, List<MoveDTO> listMoves) {
        trainerId.setItems(listTrainers);
        trainerId.setItemLabelGenerator(i -> i.getName() + " ( " + i.getId() +" )");

        movesIds.setItems(listMoves);
        movesIds.setItemLabelGenerator(MoveDTO::getName);

        id.setReadOnly(true);

        name.setRequired(true);
        type.setRequired(true);

        name.setClearButtonVisible(true);
        type.setClearButtonVisible(true);
        movesIds.setClearButtonVisible(true);
        trainerId.setClearButtonVisible(true);
    }

    private void bindDataToFields() {
        binder.forField(id).withNullRepresentation(null)
                .bind(PokemonDTO::getId, PokemonDTO::setId);
        binder.forField(name)
                .withValidator(name -> name.trim().length() != 0 , "Name can't be empty")
                .withValidator(new StringLengthValidator("Max length is 30 characters", 0, 30))
                .bind(PokemonDTO::getName, PokemonDTO::setName);
        binder.forField(type)
                .withValidator(name -> name.trim().length() != 0 , "Type can't be empty")
                .withValidator(new StringLengthValidator("Max length is 30 characters", 0, 30))
                .bind(PokemonDTO::getType, PokemonDTO::setType);
        binder.forField(trainerId).withConverter(new TrainerConverter(trainerResource)).bind(PokemonDTO::getTrainer, PokemonDTO::setTrainerId);
        binder.forField(movesIds).withConverter(new MoveConverter(moveResource)).bind(PokemonDTO::getMoves, PokemonDTO::setMovesIds);
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
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, pokemon)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return h;
    }

    private void validateAndSave() {
        try {
            binder.writeBean(pokemon);
            fireEvent(new SaveEvent(this, pokemon));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// POKEMON FROM GRID
//----------------------------------------------------------------------------------------------------------------------

    public void setPokemon(PokemonDTO pokemon) {
        this.pokemon = pokemon;
        binder.readBean(pokemon);
    }

//----------------------------------------------------------------------------------------------------------------------
// EVENTS
//----------------------------------------------------------------------------------------------------------------------

    public abstract static class PokemonFormEvent extends ComponentEvent<PokemonForm> {
        private final PokemonDTO pokemon;

        protected PokemonFormEvent(PokemonForm source, PokemonDTO pokemon) {
            super(source, false);
            this.pokemon = pokemon;
        }

        public PokemonDTO getPokemon() {
            return pokemon;
        }
    }

    public static class SaveEvent extends PokemonFormEvent {
        SaveEvent(PokemonForm source, PokemonDTO pokemon) {
            super(source, pokemon);
        }
    }

    public static class DeleteEvent extends PokemonFormEvent {
        DeleteEvent(PokemonForm source, PokemonDTO pokemon) {
            super(source, pokemon);
        }

    }

    public static class CloseEvent extends PokemonFormEvent {
        CloseEvent(PokemonForm source) {
            super(source, null);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
