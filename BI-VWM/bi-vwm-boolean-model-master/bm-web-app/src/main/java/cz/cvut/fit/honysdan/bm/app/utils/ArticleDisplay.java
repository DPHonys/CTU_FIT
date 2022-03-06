package cz.cvut.fit.honysdan.bm.app.utils;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import cz.cvut.fit.honysdan.bm.db.utils.dto.ArticleDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//----------------------------------------------------------------------------------------------------------------------
// Custom VAADIN component that allows to view the article when it's selected in grid
//----------------------------------------------------------------------------------------------------------------------
public class ArticleDisplay extends VerticalLayout {

    private final Button close = new Button("Close");
    private final H1 articleName = new H1("ARTICLE NAME");
    private final Anchor articleLink = new Anchor("https://www.wikipedia.org/", "WIKI");

    private final VerticalLayout paragraphLayout = new VerticalLayout();
    private List<Paragraph> articleParagraphs;

    public ArticleDisplay() {
        addClassName("article-display");

        paragraphLayout.addClassName("paragraph-layout");
        articleName.addClassName("article-h1");
        articleLink.addClassName("article-link");

        // sets up the close button
        close.setWidth("100%");
        close.addClassName("article-display-close");
        close.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addClickShortcut(Key.ESCAPE);
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        add(close, articleName, articleLink, paragraphLayout);
    }

    private void setParagraphs(ArticleDTO value) {
        // delete old paragraphs
        if (articleParagraphs != null && !articleParagraphs.isEmpty()) {
            for (Paragraph p : articleParagraphs)
                paragraphLayout.remove(p);
        }

        articleParagraphs = new ArrayList<>();

        // breakdown string to paragraphs
        List<String> breakdown = new ArrayList<>(Arrays.asList(value.getArticle().split("</p>")));

        // set up each paragraph
        for (String i : breakdown) {
            Paragraph tmp = new Paragraph(i);
            tmp.addClassName("article-paragraph");
            articleParagraphs.add(tmp);
        }

        // add each new paragraph to layout
        for (Paragraph p : articleParagraphs) {
            paragraphLayout.add(p);
        }
    }

    public void setArticle(ArticleDTO value) {
        // sets up the article display
        if (value != null) {
            articleName.setText(value.getName());
            articleLink.setHref(value.getLink());
            setParagraphs(value);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // EVENTS
    //------------------------------------------------------------------------------------------------------------------
    public abstract static class ArticleDisplayEvent extends ComponentEvent<ArticleDisplay> {
        private final ArticleDTO article;

        protected ArticleDisplayEvent(ArticleDisplay source, ArticleDTO article) {
            super(source, false);
            this.article = article;
        }

        public ArticleDTO getArticle() {
            return article;
        }
    }

    public static class CloseEvent extends ArticleDisplayEvent {
        CloseEvent(ArticleDisplay source) {
            super(source, null);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
