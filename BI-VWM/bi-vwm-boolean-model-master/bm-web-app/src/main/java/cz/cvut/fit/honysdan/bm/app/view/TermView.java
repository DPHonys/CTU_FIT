package cz.cvut.fit.honysdan.bm.app.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.cvut.fit.honysdan.bm.app.utils.MyLayout;
import cz.cvut.fit.honysdan.bm.db.utils.dto.TermDTO;
import cz.cvut.fit.honysdan.bm.db.utils.service.TermService;

//----------------------------------------------------------------------------------------------------------------------
// View that defines how term page is going to be laid out
//  - shows all terms and the count of their occurrences
//----------------------------------------------------------------------------------------------------------------------
@Route(value = "/term", layout = MyLayout.class)
@PageTitle("Boolean model | Term")
public class TermView extends VerticalLayout {

    private final TermService termService;

    Grid<TermDTO> grid = new Grid<>(TermDTO.class);

    public TermView(TermService termService) {
        this.termService = termService;

        addClassName("term-view");
        setSizeFull();
        configureGrid();
        updateList();

        add(grid);
    }

    private void configureGrid() {
        grid.addClassName("article-grid");
        grid.setSizeFull();
        grid.setColumns("id", "term", "count");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
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
                            return termService.getAll(offset, limit, pageSize).stream();
                        }, query -> termService.getArticleCount()
                )
        );
    }
}
