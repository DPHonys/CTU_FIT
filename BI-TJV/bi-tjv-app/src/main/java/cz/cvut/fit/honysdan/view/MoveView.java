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
import cz.cvut.fit.honysdan.entity.MoveDTO;
import cz.cvut.fit.honysdan.form.MoveForm;
import cz.cvut.fit.honysdan.notification.ErrorNotification;
import cz.cvut.fit.honysdan.resource.MoveResource;

import java.util.ArrayList;
import java.util.List;

@Route(value = "move", layout = MainLayout.class)
@PageTitle("Pokemon DB | Move")
public class MoveView extends VerticalLayout {

    MoveResource moveResource;

    Grid<MoveDTO> grid = new Grid<>(MoveDTO.class);
    TextField filterByName = new TextField();
    IntegerField filterById = new IntegerField();

    Button createTrainer = new Button("Add Move to DB");

    private final MoveForm form;

    public MoveView(MoveResource moveResource) {
        this.moveResource = moveResource;

        addClassName("move-view");
        setSizeFull();
        configureGrid();

        form = new MoveForm();
        form.addListener(MoveForm.SaveEvent.class, this::saveMove);
        form.addListener(MoveForm.DeleteEvent.class, this::deleteMove);
        form.addListener(MoveForm.CloseEvent.class, e -> {
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
// MOVE VIEW SETUP
//----------------------------------------------------------------------------------------------------------------------

    private void configureGrid() {
        grid.addClassName("move-grid");
        grid.setSizeFull();
        grid.getColumns().forEach(i -> i.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setColumns("id", "name", "description");

        grid.asSingleSelect().addValueChangeListener(evt -> editMove(evt.getValue()));
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

        createTrainer.addClickListener(click -> createMove());
        createTrainer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout h = new HorizontalLayout(filterByName, filterById, createTrainer);
        h.addClassName("toolbar");
        return h;
    }

//----------------------------------------------------------------------------------------------------------------------
// MOVE VIEW METHODS
//----------------------------------------------------------------------------------------------------------------------

    private void updateGrid() {
        List<MoveDTO> list = new ArrayList<>();
        if(!filterByName.getValue().isEmpty()) {
            filterById.setEnabled(false);
            try {
                list.add(moveResource.getMoveByName(filterByName.getValue()));
                grid.setItems(list);
            } catch (Exception e) {
                new ErrorNotification("ERROR > No such move with name " + '\"' + filterByName.getValue() + '\"');
                grid.setItems(list);
            }
        } else if (filterById.getValue() != null) {
            filterByName.setEnabled(false);
            try {
                list.add(moveResource.getMoveById(filterById.getValue()));
                grid.setItems(list);
            } catch (Exception e) {
                new ErrorNotification("ERROR > No such move with id " + filterById.getValue());
                grid.setItems(list);
            }
        } else {
            filterByName.setEnabled(true);
            filterById.setEnabled(true);
            list = moveResource.getAllMove();
            grid.setItems(list);
        }
    }

    private void closeEditor() {
        grid.asSingleSelect().clear();
        grid.deselectAll();
        form.setMove(null);
        form.setVisible(false);
        removeClassName("editing");
    }

//----------------------------------------------------------------------------------------------------------------------
// EVENTS
//----------------------------------------------------------------------------------------------------------------------

    private void deleteMove(MoveForm.DeleteEvent evt) {
        moveResource.deleteMove(evt.getMove().getId());
        updateGrid();
        closeEditor();
    }

    private void saveMove(MoveForm.SaveEvent evt) {
        if(evt.getMove().getId() == null) {
            moveResource.createMove(evt.getMove());
        } else {
            moveResource.updateMove(evt.getMove().getId(), new MoveDTO(0, "null", null));
            moveResource.updateMove(evt.getMove().getId(), evt.getMove());
        }
        updateGrid();
        closeEditor();
    }

    private void createMove() {
        grid.asSingleSelect().clear();
        editMove(new MoveDTO());
    }

    private void editMove(MoveDTO value) {
        if(value == null) {
            closeEditor();
        } else {
            form.setMove(value);
            form.setVisible(true);
            addClassName("editing");
        }
    }
}
