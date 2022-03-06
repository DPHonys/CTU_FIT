package cz.cvut.fit.honysdan.bm.preprocess.utils;

import cz.cvut.fit.honysdan.bm.db.utils.entity.Article;
import cz.cvut.fit.honysdan.bm.db.utils.entity.Term;
import cz.cvut.fit.honysdan.bm.db.utils.manager.Manager;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static cz.cvut.fit.honysdan.bm.preprocess.Main.FOLDER_PATH;

@Slf4j
public class Processor {
    private final List<String> files;
    private final List<String> stopWords;

    // Database manager
    Manager manager;

    // Dictionary of terms
    HashMap<String, Term> dictionary;

    // NLP pipeline
    StanfordCoreNLP pipeline;

    public Processor(List<String> files, List<String> stopWords) {
        this.files = files;
        this.stopWords = stopWords;

        manager = new Manager();
        dictionary = new HashMap<>();

        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        // build pipeline
        pipeline = new StanfordCoreNLP(props);
    }

    private String buildString(List<String> paragraphs) {
        StringBuilder builder = new StringBuilder();
        for (String e : paragraphs) {
            builder.append(e);
            builder.append("</p>");
        }
        return builder.toString();
    }

    private List<String> extractTerms(String text) {
        // I. Lemmatization
        // II. Tokenization
        // Create a document object
        CoreDocument document = pipeline.processToCoreDocument(text);

        // Get tokens
        List<CoreLabel> tokens = document.tokens();
        // Extracting lemmas
        ArrayList<String> lemmas = tokens.stream().map(CoreLabel::lemma).collect(Collectors.toCollection(ArrayList::new));

        // III. Removing all irrelevant characters (Numbers and Punctuation, Special characters).
        // IV. Convert all characters into lowercase.
        for (int e = 0; e < lemmas.size(); e++) {
            if (!lemmas.get(e).matches("\\A\\p{ASCII}*\\z")){
                lemmas.set(e, lemmas.get(e).replaceAll(".*", ""));
            } else {
                lemmas.set(e, lemmas.get(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
            }
        }

        // V. Removing stop words
        // VI. Remove the words having length <= 2
        for (int e = 0; e < lemmas.size(); e++) {
            if (stopWords.contains(lemmas.get(e)) || lemmas.get(e).length() <= 2) {
                lemmas.set(e, lemmas.get(e).replaceAll(".*", ""));
            }
        }

        // VII. Filter out unique words
        List<String> uniqueTerms = lemmas.stream().distinct().collect(Collectors.toList());
        uniqueTerms.removeAll(Arrays.asList("", null));
        return uniqueTerms;
    }

    private void saveData(String name, String link, String text, List<String> terms) {
        // Update dictionary, update or save terms in database
        List<Term> termList = new ArrayList<>();
        for (String t : terms) {
            if (dictionary.containsKey(t)) {
                // if term is in dictionary then it is present in database
                Term term = dictionary.get(t);
                termList.add(term);
                int count = term.getCount();
                dictionary.get(t).setCount(count + 1);
                manager.updateTermCount(dictionary.get(t).getId());
            } else {
                // Create new term
                Term term = new Term();
                term.setCount(1);
                term.setTerm(t);

                // Save new term
                manager.createObject(term);
                dictionary.put(t, term);

                // add it to list
                termList.add(term);
            }
        }

        // Save article
        Article article = new Article();
        article.setName(name);
        article.setLink(link);
        article.setArticle(text);
        article.setTerms(termList);

        manager.createObject(article);
    }

    public void run() throws ExceptionInInitializerError, HibernateException, FileNotFoundException {
        // Database setup
        manager.setup();

        // Iterate over files
        for (String file : files) {
            // Load data from a file
            String name, link;
            List<String> paragraphs = new ArrayList<>();
            try (Scanner scanner = new Scanner(new File(FOLDER_PATH, file))) {
                name = scanner.nextLine();
                link = scanner.nextLine();
                while (scanner.hasNextLine()) {
                    paragraphs.add(scanner.nextLine());
                }
            }
            log.info("Loaded - " + name);

            // Build one long string
            String longString = buildString(paragraphs);
            // Get unique terms from article's text
            List<String> articleTerms = extractTerms(longString);
            log.info("Processed - " + name);

            // Save data into database
            saveData(name, link, longString, articleTerms);
            log.info("Saved - " + name);
        }

        // End database connection
        manager.exit();
    }
}
