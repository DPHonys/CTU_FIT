package cz.cvut.fit.honysdan.utils;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.cvut.fit.honysdan.view.MoveView;
import cz.cvut.fit.honysdan.view.PokemonView;
import cz.cvut.fit.honysdan.view.TrainerView;

@CssImport("./styles/shared-styles.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createDrawer() {
        RouterLink pokemon = new RouterLink("Pokemon", PokemonView.class);
        pokemon.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink trainer = new RouterLink("Trainer", TrainerView.class);
        trainer.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink move = new RouterLink("Move", MoveView.class);
        move.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(pokemon, trainer, move));
    }

    private void createHeader() {
        H1 logo = new H1("Pokemon DB");
        logo.addClassName("logo");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.addClassName("header");
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }
}
