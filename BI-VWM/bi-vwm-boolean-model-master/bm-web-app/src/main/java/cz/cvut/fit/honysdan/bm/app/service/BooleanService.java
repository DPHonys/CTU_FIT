package cz.cvut.fit.honysdan.bm.app.service;

import cz.cvut.fit.honysdan.bm.app.antlr.BOOLEANBaseVisitor;
import cz.cvut.fit.honysdan.bm.app.antlr.BOOLEANLexer;
import cz.cvut.fit.honysdan.bm.app.antlr.BOOLEANParser;
import cz.cvut.fit.honysdan.bm.db.utils.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BooleanService {

    //------------------------------------------------------------------------------------------------------------------
    // Parser logic
    //------------------------------------------------------------------------------------------------------------------

    // Method tries to parse passed string into a Node tree
        // if any error in query occurs (query doesn't comply with grammar) an exception is thrown
    public Node parse(String query) throws Exception {
        // ANTLR stuff
        CharStream charStream = CharStreams.fromString(query);

        BOOLEANLexer booleanLexer = new BOOLEANLexer(charStream);
        booleanLexer.removeErrorListeners();
        booleanLexer.addErrorListener(ThrowingErrorListener.INSTANCE);

        CommonTokenStream commonTokenStream = new CommonTokenStream(booleanLexer);

        BOOLEANParser booleanParser = new BOOLEANParser(commonTokenStream);
        booleanParser.removeErrorListeners();
        booleanParser.addErrorListener(ThrowingErrorListener.INSTANCE);

        ParseTree parserTree = booleanParser.start();

        // Final check if any error occurred but was not thrown
        if (booleanParser.getNumberOfSyntaxErrors() != 0) {
            throw new Exception("Parser couldn't process query");
        }

        // Transform ANTLR parse tree into Node tree with visitor
        MyVisitor visitor = new MyVisitor();
        return visitor.visit(parserTree);
    }

    // Custom error listener for ANTLR parser
    public static class ThrowingErrorListener extends BaseErrorListener {

        public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                throws ParseCancellationException {
            throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

    // Custom visitor that transforms ANTLR parse tree into a Node tree that can get evaluated
    class MyVisitor extends BOOLEANBaseVisitor<Node> {

        @Override
        // starting point of a tree
        public Node visitStart(BOOLEANParser.StartContext ctx) {
            return visit(ctx.expression());
        }

        @Override
        // Expression -> Token OR Expression | Token
        public Node visitExpression(BOOLEANParser.ExpressionContext ctx) {
            int children = ctx.getChildCount();

            if (children == 3) {
                Node left = visit(ctx.token());
                Node right = visit(ctx.expression());
                return new OrNode(left, right);
            } else {
                return visit(ctx.token());
            }
        }

        @Override
        // Token -> Factor AND Token | Factor
        public Node visitToken(BOOLEANParser.TokenContext ctx) {
            int children = ctx.getChildCount();

            if (children == 3) {
                Node left = visit(ctx.factor());
                Node right = visit(ctx.token());
                return new AndNode(left, right);
            } else {
                return visit(ctx.factor());
            }
        }

        @Override
        // Factor -> TERM | NOT Factor | ( Expression )
        public Node visitFactor(BOOLEANParser.FactorContext ctx) {
            int children = ctx.getChildCount();

            if (children == 3) {
                return visit(ctx.expression());
            } else if (children == 2) {
                return new NotNode(visit(ctx.factor()));
            } else {
                return new TermNode(ctx.getText());
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // NODES - used to evaluate query and get list of articles that should comply with the boolean query
    //------------------------------------------------------------------------------------------------------------------

    public abstract class Node {
        // Method to override for evaluating node
        public abstract List<Integer> eval(ArticleService articleService, boolean useIndexes);
    }

    abstract class UnaryNode extends Node {
        private final Node expression;

        public Node getExpression() {
            return expression;
        }

        protected UnaryNode(Node expression) {
            this.expression = expression;
        }
    }

    abstract class BinaryNode extends Node {
        private final Node leftExpression;
        private final Node rightExpression;

        public Node getLeftExpression() {
            return leftExpression;
        }
        public Node getRightExpression() {
            return rightExpression;
        }

        protected BinaryNode(Node leftExpression, Node rightExpression) {
            this.leftExpression = leftExpression;
            this.rightExpression = rightExpression;
        }
    }

    class AndNode extends BinaryNode {
        public AndNode(Node leftExpression, Node rightExpression) {
            super(leftExpression, rightExpression);
        }

        @Override
        public List<Integer> eval(ArticleService articleService, boolean useIndexes) {
            List<Integer> retLeft = getLeftExpression().eval(articleService, useIndexes);
            List<Integer> retRight = getRightExpression().eval(articleService, useIndexes);

            List<Integer> ret = new ArrayList<>();

            int i = 0;
            int ii = 0;

            // merge two lists into one (sorted)
            while (i != retLeft.size() && ii != retRight.size()) {
                int left = retLeft.get(i);
                int right = retRight.get(ii);

                if (left != right) {
                    if (left < right) {
                        i = i + 1;
                    } else {
                        ii = ii + 1;
                    }
                } else {
                    ret.add(right);
                    if (i < ii) {
                        i = i + 1;
                    } else {
                        ii = ii + 1;
                    }
                }
            }

            return ret;
        }
    }

    class OrNode extends BinaryNode {
        public OrNode(Node leftExpression, Node rightExpression) {
            super(leftExpression, rightExpression);
        }

        @Override
        public List<Integer> eval(ArticleService articleService, boolean useIndexes) {
            List<Integer> retLeft = getLeftExpression().eval(articleService, useIndexes);
            List<Integer> retRight = getRightExpression().eval(articleService, useIndexes);

            if ( retLeft.size() > retRight.size() ) {
                List<Integer> biggerListCopy = new ArrayList<>(retLeft);
                biggerListCopy.removeAll(retRight);
                retRight.addAll(biggerListCopy);

                Collections.sort(retRight);
                return retRight;
            } else {
                List<Integer> biggerListCopy = new ArrayList<>(retRight);
                biggerListCopy.removeAll(retLeft);
                retLeft.addAll(biggerListCopy);

                Collections.sort(retLeft);
                return retLeft;
            }
        }
    }

    class NotNode extends UnaryNode {
        public NotNode(Node expression) {
            super(expression);
        }

        @Override
        public List<Integer> eval(ArticleService articleService, boolean useIndexes) {
            List<Integer> ret = getExpression().eval(articleService, useIndexes);
            int end = articleService.getArticleCount();
            List<Integer> range = IntStream.rangeClosed(1, end).boxed().collect(Collectors.toList());
            for (Integer i : ret) {
                range.remove(i);
            }
            return range;
        }
    }

    class TermNode extends Node {
        private final String term;

        public String getTerm() {
            return term;
        }

        public TermNode(String term) {
            this.term = term;
        }

        @Override
        public List<Integer> eval(ArticleService articleService, boolean useIndexes) {
            if (useIndexes) {
                return articleService.getIDsOfArticlesWithIndex(term);
            } else {
                return articleService.getIDsOfArticlesWithNoIndex(term);
            }
        }
    }

}
