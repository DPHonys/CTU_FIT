package cz.cvut.fit.honysdan.bm.app.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.cvut.fit.honysdan.bm.app.notification.ErrorNotification;
import cz.cvut.fit.honysdan.bm.app.utils.ArticleDisplay;
import cz.cvut.fit.honysdan.bm.app.utils.MyLayout;
import cz.cvut.fit.honysdan.bm.db.utils.dto.ArticleDTO;
import cz.cvut.fit.honysdan.bm.db.utils.service.ArticleService;
import cz.cvut.fit.honysdan.bm.app.service.BooleanService;

import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------------------------------------------------------
// View that defines how query page is going to be laid out
//  - lets the user perform boolean queries on the database
//  - if the query is wrong the user is notified with an error notification and FAILED sparser state
//  - if query passes parser it gets performed and the user sees the final list of articles and
//    how much time it took to get the result
//  - after performing query user can open an article and read it
//----------------------------------------------------------------------------------------------------------------------
@Route(value = "/query", layout = MyLayout.class)
@PageTitle("Boolean model | Query")
public class QueryView extends VerticalLayout {

    private final ArticleService articleService;
    private final BooleanService booleanService;
    private final ArticleDisplay display;

    TextField query = new TextField();
    Button submit = new Button("SUBMIT");
    Grid<ArticleDTO> grid = new Grid<>(ArticleDTO.class);

    Paragraph parse = new Paragraph("PARSING :");
    Paragraph timeIndex = new Paragraph("TIME (with indexes) :");
    Paragraph timeNoIndex = new Paragraph("TIME (no indexes) :");
    Paragraph count = new Paragraph("COUNT :");

    Button parseOutcome = new Button("---");
    Button timeIndexOutcome = new Button("---");
    Button timeNoIndexOutcome = new Button("---");
    Button countOutcome = new Button("---");

    ProgressBar parseBar = new ProgressBar();
    ProgressBar timeIndexBar = new ProgressBar();
    ProgressBar timeNoIndexBar = new ProgressBar();

    HorizontalLayout parserLayout = new HorizontalLayout();
    HorizontalLayout timeIndexLayout = new HorizontalLayout();
    HorizontalLayout timeNoIndexLayout = new HorizontalLayout();
    HorizontalLayout countLayout = new HorizontalLayout();

    public QueryView(ArticleService articleService, BooleanService booleanService) {
        this.articleService = articleService;
        this.booleanService = booleanService;

        addClassName("query-view");
        setSizeFull();

        configureQueryFiled();
        configureSubmitButton();
        setupGrid();

        display = new ArticleDisplay();
        display.addListener(ArticleDisplay.CloseEvent.class, event -> closeDisplay());

        parseBar.setIndeterminate(true);
        parseBar.addClassNames("info-block-bar");
        timeIndexBar.setIndeterminate(true);
        timeIndexBar.addClassNames("info-block-bar");
        timeNoIndexBar.setIndeterminate(true);
        timeNoIndexBar.addClassNames("info-block-bar");

        parse.addClassName("info-block-text");
        timeIndex.addClassName("info-block-text");
        timeNoIndex.addClassName("info-block-text");
        count.addClassName("info-block-text");

        parseBar.setVisible(false);
        parserLayout.add(parse, parseOutcome, parseBar);
        parserLayout.addClassName("info-block");

        timeIndexBar.setVisible(false);
        timeIndexLayout.add(timeIndex, timeIndexOutcome, timeIndexBar);
        timeIndexLayout.addClassName("info-block");

        timeNoIndexBar.setVisible(false);
        timeNoIndexLayout.add(timeNoIndex, timeNoIndexOutcome, timeNoIndexBar);
        timeNoIndexLayout.addClassName("info-block");

        countLayout.add(count, countOutcome);
        countLayout.addClassName("info-block");

        countOutcome.addClassName("outcome-count");

        VerticalLayout main = new VerticalLayout();
        main.addClassName("main");
        main.setSizeFull();
        main.setPadding(false);
        main.add(query,
                 submit,
                 parserLayout,
                 timeIndexLayout,
                 timeNoIndexLayout,
                 countLayout,
                 grid);

        Div content = new Div(main, display);
        content.addClassName("content");
        content.setSizeFull();

        add(content);
        closeDisplay();
    }

    private void setupGrid() {
        grid.addClassName("article-grid");
        grid.setSizeFull();
        grid.setColumns("id", "name");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        cleanseGrid();
        grid.asSingleSelect().addValueChangeListener(event -> displayArticle(event.getValue()));
    }

    private void cleanseGrid() {
        grid.setItems(new ArrayList<>());
    }

    private void fullGrid() {
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

    private void configureSubmitButton() {
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submit.addClickListener(click -> submitQuery());
        submit.addClickShortcut(Key.ENTER);
    }

    private void submitQuery() {
        cleanseGrid();
        resetOutcomes();
        if( query.getValue() == null || query.isEmpty() || query.getValue().trim().isEmpty()) {
            fullGrid();
        } else {
            parseBar.setVisible(true);

            BooleanService.Node ret;
            try {
                ret = booleanService.parse(query.getValue() + '\n' );
            } catch (Exception ex) {
                // Parsing failed
                parseBar.setVisible(false);
                parseError();
                new ErrorNotification(ex.getMessage());
                return;
            }
            // Parsing success
            parseBar.setVisible(false);
            parseSuccess();

            // Eval query with index
            timeIndexBar.setVisible(true);

            long startTime = System.currentTimeMillis();
            List<Integer> list = ret.eval(articleService, true);
            long timeElapsed = System.currentTimeMillis() - startTime;

            timeIndexBar.setVisible(false);
            timeIndexOutcome.setText(timeElapsed + "ms");

            int pageSize = 50;
            grid.setPageSize(pageSize);
            grid.setDataProvider(
                    DataProvider.fromCallbacks(
                            query -> {
                                int offset = query.getOffset();
                                int limit = query.getLimit();
                                return articleService.getByIds(offset, limit, pageSize, list).stream();
                            }, query -> list.size()
                    )
            );

            // Eval query with no index
            timeNoIndexBar.setVisible(true);

            long startTime2 = System.currentTimeMillis();
            ret.eval(articleService, false);
            long timeElapsed2 = System.currentTimeMillis() - startTime2;

            timeNoIndexBar.setVisible(false);
            timeNoIndexOutcome.setText(timeElapsed2 + "ms");

            countOutcome.setText(String.valueOf(list.size()));
        }
    }

    private void parseSuccess() {
        parseOutcome.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        parseOutcome.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        parseOutcome.setText("PASS");
    }

    private void parseError() {
        parseOutcome.addThemeVariants(ButtonVariant.LUMO_ERROR);
        parseOutcome.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        parseOutcome.setText("ERROR");
    }

    private void resetOutcomes() {
        parseOutcome.removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
        parseOutcome.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        parseOutcome.removeThemeVariants(ButtonVariant.LUMO_ERROR);
        parseOutcome.setText("---");
        timeIndexOutcome.setText("---");
        timeNoIndexOutcome.setText("---");
        countOutcome.setText("---");
    }

    private void configureQueryFiled() {
        query.setPlaceholder("bangladesh OR november");
        query.setClearButtonVisible(true);
        query.addClassName("submit-button");
        query.setWidthFull();
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
