package cz.cvut.fit.honysdan.bm.app.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.cvut.fit.honysdan.bm.app.utils.MyLayout;

//----------------------------------------------------------------------------------------------------------------------
// View that defines how home page is going to be laid out
//  - shows basic information about project and how to write a query
//----------------------------------------------------------------------------------------------------------------------
@Route(value = "/", layout = MyLayout.class)
@PageTitle("Boolean model | Home")
public class HomeView extends VerticalLayout {

    H1 welcome = new H1("WELCOME");
    Anchor anchor = new Anchor("mailto:honysdan@fit.cvut.cz", "honysdan@fit.cvut.cz");
    Paragraph welcomeParagraph = new Paragraph(new Text ("This is semestral project for Information Retrieval and Web Search course " +
            "developed by Daniel Honys "), anchor, new Text (" . This page is a showcasing of my solution to the Boolean " +
            "model of information retrieval.\n"));
    H2 query = new H2("QUERY");
    Paragraph queryParagraph = new Paragraph("On this page, you can try to perform a boolean query on the database. The " +
            "query is performed only if it passes parsing. After performing the query you can see how much time did the " +
            "query take to process with and without index.\n");
    H3 rules = new H3("Rules for query");
    ListItem rule1 = new ListItem("1. Every term needs to be in lower case");
    ListItem rule2 = new ListItem("2. Every boolean operator need to be upper case (AND | OR | NOT)");
    ListItem rule3 = new ListItem("3. Every opening parenthesis needs to be closed at some point");
    UnorderedList rulesList = new UnorderedList(rule1, rule2, rule3);
    H3 examples = new H3("Examples");
    ListItem example1 = new ListItem("1. \"bangladesh\"");
    ListItem example2 = new ListItem("2. \"bangladesh OR november\"");
    ListItem example3 = new ListItem("3. \"NOT (bangladesh AND price OR NOT july)\"");
    UnorderedList exampleList = new UnorderedList(example1, example2, example3);
    H2 term = new H2("TERM");
    Paragraph termParagraph = new Paragraph("Lists all terms used in articles and their number of occurrences.");
    H2 article = new H2("ARTICLE");
    Paragraph articleParagraph = new Paragraph("Lists all articles in the database.");

    public HomeView() {
        add(welcome, welcomeParagraph, query, queryParagraph, rules, rulesList, examples, exampleList, term,
                termParagraph, article, articleParagraph);
    }
}
