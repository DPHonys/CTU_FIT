package cz.cvut.fit.honysdan.bm.app.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.cvut.fit.honysdan.bm.app.utils.ArticleDisplay;
import cz.cvut.fit.honysdan.bm.app.utils.MyLayout;
import cz.cvut.fit.honysdan.bm.db.utils.dto.ArticleDTO;
import cz.cvut.fit.honysdan.bm.db.utils.service.ArticleService;

//----------------------------------------------------------------------------------------------------------------------
// View that defines how article page is going to be laid out
//  - shows all articles and lets the user read an article when it gets selected in the grid
//----------------------------------------------------------------------------------------------------------------------
@Route(value = "/article", layout = MyLayout.class)
@PageTitle("Boolean model | Article")
public class ArticleView extends VerticalLayout {

    private final ArticleService articleService;

    Grid<ArticleDTO> grid = new Grid<>(ArticleDTO.class);
    private final ArticleDisplay display;

    ArticleDTO defaultRow;

    public ArticleView(ArticleService articleService) {
        this.articleService = articleService;

        addClassName("article-view");
        setSizeFull();
        display = new ArticleDisplay();
        display.addListener(ArticleDisplay.CloseEvent.class, event -> closeDisplay());

        configureGrid();

        Div content = new Div(grid, display);
        content.addClassName("content");
        content.setSizeFull();

        add(content);
    }

    private void updateList() {
        // sets up grid content and pagination of the content
        int pageSize = 50;

        grid.setPageSize(pageSize);
        grid.setDataProvider(
                DataProvider.fromCallbacks(
                        query -> {
                            int offset = query.getOffset();
                            int limit = query.getLimit();
                            return articleService.getAll(offset, limit, pageSize).stream();
                        }, query -> articleService.getArticleCount()
                )
        );
    }

    private void configureGrid() {
        grid.addClassName("article-grid");
        grid.setSizeFull();
        grid.setColumns("id", "name");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        updateList();

        // select first article in grid and display its content
        if( articleService.getByID(1).isPresent() ) {
            defaultRow = articleService.getByID(1).get();
            grid.select(defaultRow);
            displayArticle(defaultRow);
        }

        grid.asSingleSelect().addValueChangeListener(event -> displayArticle(event.getValue()));
    }

    private void displayArticle(ArticleDTO value) {
        if (value == null) {
            // if article gets deselected > close display
            closeDisplay();
        } else {
            display.setArticle(value);
            display.setVisible(true);
            addClassName("display");
        }
    }

    private void closeDisplay() {
        grid.deselectAll();
        display.setArticle(null);
        display.setVisible(false);
        removeClassName("display");
    }
}
