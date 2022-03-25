package cz.cvut.fit.honysdan.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.cvut.fit.honysdan.utils.MainLayout;
import cz.cvut.fit.honysdan.entity.MoveDTO;
import cz.cvut.fit.honysdan.form.PokemonForm;
import cz.cvut.fit.honysdan.notification.ErrorNotification;
import cz.cvut.fit.honysdan.entity.PokemonDTO;
import cz.cvut.fit.honysdan.notification.SuccessNotification;
import cz.cvut.fit.honysdan.resource.MoveResource;
import cz.cvut.fit.honysdan.resource.PokemonResource;
import cz.cvut.fit.honysdan.resource.TrainerResource;

import java.util.ArrayList;
import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Pokemon DB | Pokemon")
public class PokemonView extends VerticalLayout {

    PokemonResource pokemonResource;
    TrainerResource trainerResource;
    MoveResource moveResource;

    Grid<PokemonDTO> grid = new Grid<>(PokemonDTO.class);
    TextField filterByName = new TextField();
    IntegerField filterById = new IntegerField();

    Button createPokemon = new Button("Add Pokemon to DB");
    Button addMove = new Button("Add Move");
    Button removeMove = new Button("Remove Move");

    private final PokemonForm form;

    public PokemonView(PokemonResource pokemonResource, TrainerResource trainerResource, MoveResource moveResource) {
        this.pokemonResource = pokemonResource;
        this.trainerResource = trainerResource;
        this.moveResource = moveResource;

        addClassName("pokemon-view");
        setSizeFull();
        configureGrid();

        form = new PokemonForm(trainerResource.getAllTrainer(), moveResource.getAllMove(), trainerResource, moveResource);
        form.addListener(PokemonForm.SaveEvent.class, this::savePokemon);
        form.addListener(PokemonForm.DeleteEvent.class, this::deletePokemon);
        form.addListener(PokemonForm.CloseEvent.class, e -> {
            updateGrid();
            closeEditor();
        });

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(toolBar(), content);
        updateGrid();
        closeEditor();
    }

//----------------------------------------------------------------------------------------------------------------------
// POKEMON VIEW SETUP
//----------------------------------------------------------------------------------------------------------------------

    private void configureGrid() {
        grid.addClassName("pokemon-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("trainer");
        grid.removeColumnByKey("moves");
        grid.setColumns("id", "name", "type");

        grid.addColumn(this::pokemonMoves).setHeader("Moves");
        grid.addColumn(this::getTrainer).setHeader("Trainer");

        grid.getColumns().forEach(i -> i.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

        grid.asSingleSelect().addValueChangeListener(evt -> editPokemon(evt.getValue()));
    }

    private HorizontalLayout toolBar() {
        filterByName.setPlaceholder("Filter by name...");
        filterByName.setClearButtonVisible(true);
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        filterByName.addValueChangeListener(e -> updateGrid());
        filterByName.setClearButtonVisible(true);

        filterById.setPlaceholder("Filter by id...");
        filterById.setClearButtonVisible(true);
        filterById.setValueChangeMode(ValueChangeMode.LAZY);
        filterById.addValueChangeListener(e -> updateGrid());
        filterById.setClearButtonVisible(true);

        createPokemon.addClickListener(click -> creatPokemon());
        createPokemon.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addMove.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        removeMove.addThemeVariants(ButtonVariant.LUMO_ERROR);

        addMove.addClickListener(click -> addMove());
        removeMove.addClickListener(click -> removeMove());

        HorizontalLayout h = new HorizontalLayout(filterByName, filterById, createPokemon, addMove, removeMove);
        h.addClassName("toolbar");
        return h;
    }

//----------------------------------------------------------------------------------------------------------------------
// POKEMON VIEW METHODS
//----------------------------------------------------------------------------------------------------------------------

    private List<String> pokemonMoves(PokemonDTO pokemon) {
        List<String> moveList = new ArrayList<>();

        if (!pokemon.getMoves().isEmpty()) {
            for (Integer i : pokemon.getMoves()) {
                moveList.add(moveResource.getMoveById(i).getName());
            }
        }

        return moveList;
    }

    private String getTrainer(PokemonDTO pokemon) {
        if(pokemon.getTrainer() != null) {
            return trainerResource.getTrainerById(pokemon.getTrainer()).getName();
        }
        return "-";
    }

    private void updateGrid() {
        List<PokemonDTO> list = new ArrayList<>();
        if(!filterByName.getValue().isEmpty()) {
            filterById.setEnabled(false);
            try {
                list = pokemonResource.getPokemonByName(filterByName.getValue());
                grid.setItems(list);
            } catch (Exception e) {
                new ErrorNotification("ERROR > No such pokemon with name " + '\"' + filterByName.getValue() + '\"');
                grid.setItems(list);
            }
        } else if (filterById.getValue() != null) {
            filterByName.setEnabled(false);
            try {
                list.add(pokemonResource.getPokemonById(filterById.getValue()));
                grid.setItems(list);
            } catch (Exception e) {
                new ErrorNotification("ERROR > No such pokemon with id " + filterById.getValue());
                grid.setItems(list);
            }
        } else {
            filterByName.setEnabled(true);
            filterById.setEnabled(true);
            list = pokemonResource.getAllPokemon();
            grid.setItems(list);
        }
    }

    private void closeEditor() {
        grid.asSingleSelect().clear();
        grid.deselectAll();
        form.setPokemon(null);
        form.setVisible(false);
        removeClassName("editing");
    }

//----------------------------------------------------------------------------------------------------------------------
// EVENTS
//----------------------------------------------------------------------------------------------------------------------

    private void deletePokemon(PokemonForm.DeleteEvent evt) {
        pokemonResource.deletePokemon(evt.getPokemon().getId());
        updateGrid();
        closeEditor();
    }

    private void savePokemon(PokemonForm.SaveEvent evt) {
        if(evt.getPokemon().getId() == null) {
            pokemonResource.createPokemon(evt.getPokemon());
        } else {
            pokemonResource.updatePokemon(evt.getPokemon().getId(), evt.getPokemon());
        }
        updateGrid();
        closeEditor();
    }

    private void creatPokemon() {
        grid.asSingleSelect().clear();
        editPokemon(new PokemonDTO());
    }

    private void editPokemon(PokemonDTO value) {
        if(value == null) {
            closeEditor();
        } else {
            form.setPokemon(value);
            form.setVisible(true);
            addClassName("editing");
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// ADD/REMOVE MOVE
//----------------------------------------------------------------------------------------------------------------------

    void addMove() {
        updateGrid();
        closeEditor();

        Dialog dialog = new Dialog();

        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.setWidth("500px");
        dialog.setHeight("360px");

        H2 addMove = new H2("ADD MOVE");
        addMove.setClassName("add-move");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        vLayout.add(addMove);

        H3 p = new H3("Pokemon");
        H3 m = new H3("Move");

        Button add = new Button("Add");
        Button cancel = new Button("Cancel");

        ComboBox<PokemonDTO> pbox = new ComboBox<>();
        pbox.addClassName("p-box");
        pbox.setWidth("100%");
        pbox.setItems(pokemonResource.getAllPokemon());
        pbox.setItemLabelGenerator(i -> i.getName() + " ( " + i.getId() +" )");
        pbox.setClearButtonVisible(true);

        ComboBox<MoveDTO> mbox = new ComboBox<>();
        mbox.addClassName("m-box");
        mbox.setWidth("100%");
        mbox.setItems(moveResource.getAllMove());
        mbox.setItemLabelGenerator(MoveDTO::getName);
        mbox.setClearButtonVisible(true);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(add, cancel);

        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // listeners
        cancel.addClickListener(click -> {
            dialog.close();
            updateGrid();
        });

        add.addClickListener(click -> {
            try {
                pokemonResource.addMove(pbox.getValue().getId(), mbox.getValue().getId());
                new SuccessNotification("Move was added to pokemon");
            } catch (Exception e) {
                new ErrorNotification("ERORR > Pokemon already knows the move");
            }
            dialog.close();
            updateGrid();
        });

        vLayout.add(p, pbox, m, mbox, buttons);
        dialog.add(vLayout);
        dialog.open();
    }

    void removeMove() {
        updateGrid();
        closeEditor();

        Dialog dialog = new Dialog();

        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.setWidth("500px");
        dialog.setHeight("360px");

        H2 addMove = new H2("REMOVE MOVE");
        addMove.setClassName("remove-move");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        vLayout.add(addMove);

        H3 p = new H3("Pokemon");
        H3 m = new H3("Move");

        Button remove = new Button("Remove");
        Button cancel = new Button("Cancel");

        ComboBox<PokemonDTO> pbox = new ComboBox<>();
        pbox.addClassName("p-box");
        pbox.setWidth("100%");
        pbox.setItems(pokemonResource.getAllPokemon());
        pbox.setItemLabelGenerator(i -> i.getName() + " ( " + i.getId() +" )");
        pbox.setClearButtonVisible(true);

        ComboBox<MoveDTO> mbox = new ComboBox<>();
        mbox.addClassName("m-box");
        mbox.setWidth("100%");
        mbox.setItems(moveResource.getAllMove());
        mbox.setItemLabelGenerator(MoveDTO::getName);
        mbox.setClearButtonVisible(true);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(remove, cancel);

        remove.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // listeners
        cancel.addClickListener(click -> {
            dialog.close();
            updateGrid();
        });

        remove.addClickListener(click -> {
            try {
                pokemonResource.removeMove(pbox.getValue().getId(), mbox.getValue().getId());
                new SuccessNotification("Move was removed from pokemon");
            } catch (Exception e) {
                new ErrorNotification("ERORR > Pokemon didn't know the move");
            }
            dialog.close();
            updateGrid();
        });

        vLayout.add(p, pbox, m, mbox, buttons);
        dialog.add(vLayout);
        dialog.open();
    }

}
