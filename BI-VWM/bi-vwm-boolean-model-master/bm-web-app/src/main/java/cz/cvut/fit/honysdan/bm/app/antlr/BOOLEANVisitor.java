package cz.cvut.fit.honysdan.bm.app.antlr;

// Generated from BOOLEAN.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BOOLEANParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BOOLEANVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BOOLEANParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(BOOLEANParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link BOOLEANParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(BOOLEANParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BOOLEANParser#token}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToken(BOOLEANParser.TokenContext ctx);
	/**
	 * Visit a parse tree produced by {@link BOOLEANParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(BOOLEANParser.FactorContext ctx);
}