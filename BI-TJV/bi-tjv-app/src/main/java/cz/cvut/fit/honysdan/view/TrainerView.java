package cz.cvut.fit.honysdan.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.cvut.fit.honysdan.utils.MainLayout;
import cz.cvut.fit.honysdan.entity.TrainerDTO;
import cz.cvut.fit.honysdan.form.TrainerForm;
import cz.cvut.fit.honysdan.notification.ErrorNotification;
import cz.cvut.fit.honysdan.resource.TrainerResource;

import java.util.ArrayList;
import java.util.List;

@Route(value = "trainer", layout = MainLayout.class)
@PageTitle("Pokemon DB | Trainer")
public class TrainerView extends VerticalLayout {

    TrainerResource trainerResource;

    Grid<TrainerDTO> grid = new Grid<>(TrainerDTO.class);
    TextField filterByName = new TextField();
    IntegerField filterById = new IntegerField();

    Button createTrainer = new Button("Add Trainer to DB");

    private final TrainerForm form;

    public TrainerView(TrainerResource trainerResource) {
        this.trainerResource = trainerResource;

        addClassName("trainer-view");
        setSizeFull();
        configureGrid();

        form = new TrainerForm();
        form.addListener(TrainerForm.SaveEvent.class, this::saveTrainer);
        form.addListener(TrainerForm.DeleteEvent.class, this::deleteTrainer);
        form.addListener(TrainerForm.CloseEvent.class, e -> {
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
// TRAINER VIEW SETUP
//----------------------------------------------------------------------------------------------------------------------

    private void configureGrid() {
        grid.addClassName("trainer-grid");
        grid.setSizeFull();
        grid.getColumns().forEach(i -> i.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.removeColumnByKey("number");
        grid.removeColumnByKey("address");
        grid.setColumns("id", "name");
        grid.addColumn(i -> {
            if ( i.getNumber() == 0 )
                return null;
            return i.getNumber();
        }).setHeader("Number");
        grid.addColumn(TrainerDTO::getAddress).setHeader("Address");

        grid.asSingleSelect().addValueChangeListener(evt -> editTrainer(evt.getValue()));
    }

    private Component toolBar() {
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

        createTrainer.addClickListener(click -> createTrainer());
        createTrainer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout h = new HorizontalLayout(filterByName, filterById, createTrainer);
        h.addClassName("toolbar");
        return h;
    }

//----------------------------------------------------------------------------------------------------------------------
// TRAINER VIEW METHODS
//----------------------------------------------------------------------------------------------------------------------

    private void updateGrid() {
        List<TrainerDTO> list = new ArrayList<>();
        if(!filterByName.getValue().isEmpty()) {
            filterById.setEnabled(false);
            try {
                list = trainerResource.getTrainerByName(filterByName.getValue());
                grid.setItems(list);
            } catch (Exception e) {
                new ErrorNotification("ERROR > No such trainer with name " + '\"' + filterByName.getValue() + '\"');
                grid.setItems(list);
            }
        } else if (filterById.getValue() != null) {
            filterByName.setEnabled(false);
            try {
                list.add(trainerResource.getTrainerById(filterById.getValue()));
                grid.setItems(list);
            } catch (Exception e) {
                new ErrorNotification("ERROR > No such trainer with id " + filterById.getValue());
                grid.setItems(list);
            }
        } else {
            filterByName.setEnabled(true);
            filterById.setEnabled(true);
            list = trainerResource.getAllTrainer();
            grid.setItems(list);
        }
    }

    private void closeEditor() {
        grid.asSingleSelect().clear();
        grid.deselectAll();
        form.setTrainer(null);
        form.setVisible(false);
        removeClassName("editing");
    }

//----------------------------------------------------------------------------------------------------------------------
// EVENTS
//----------------------------------------------------------------------------------------------------------------------

    private void deleteTrainer(TrainerForm.DeleteEvent evt) {
        trainerResource.deleteTrainer(evt.getTrainer().getId());
        updateGrid();
        closeEditor();
    }

    private void saveTrainer(TrainerForm.SaveEvent evt) {
        if(evt.getTrainer().getId() == null) {
            trainerResource.createTrainer(evt.getTrainer());
        } else {
            trainerResource.updateTrainer(evt.getTrainer().getId(), evt.getTrainer());
        }
        updateGrid();
        closeEditor();
    }

    private void createTrainer() {
        grid.asSingleSelect().clear();
        editTrainer(new TrainerDTO());
    }

    private void editTrainer(TrainerDTO value) {
        if(value == null) {
            closeEditor();
        } else {
            form.setTrainer(value);
            form.setVisible(true);
            addClassName("editing");
        }
    }

}
