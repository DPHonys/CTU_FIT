package cz.cvut.fit.honysdan.bm.app.utils;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.cvut.fit.honysdan.bm.app.view.ArticleView;
import cz.cvut.fit.honysdan.bm.app.view.HomeView;
import cz.cvut.fit.honysdan.bm.app.view.QueryView;
import cz.cvut.fit.honysdan.bm.app.view.TermView;

//----------------------------------------------------------------------------------------------------------------------
// Custom VAADIN layout that tells Vaadin how to display app in browser
//----------------------------------------------------------------------------------------------------------------------
@CssImport("./styles/shared-styles.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MyLayout extends AppLayout {
    public MyLayout() {
        createHeader();
        createDrawer();
    }

    private void createDrawer() {
        RouterLink home = new RouterLink("Home", HomeView.class);
        home.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink query = new RouterLink("Query", QueryView.class);
        query.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink term = new RouterLink("Terms", TermView.class);
        term.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink article = new RouterLink("Articles", ArticleView.class);
        article.setHighlightCondition(HighlightConditions.sameLocation());

        VerticalLayout ver = new VerticalLayout(home, query, term, article);
        ver.addClassName("ver-lay-draw");

        addToDrawer(ver);
    }

    private void createHeader() {
        H1 logo = new H1("Boolean Model");
        logo.addClassName("logo");

        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("draw-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout header = new HorizontalLayout(toggle, logo);
        header.addClassName("header");
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }
}
