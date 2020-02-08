package site.hitry.responsebin.service

import com.google.gson.JsonElement
import com.sun.org.apache.xpath.internal.operations.Bool

class ExpressionParser (
    var json: JsonElement
)
{
    companion object {
        const val TOKEN_TRUE: String = "true"
        const val TOKEN_FALSE: String = "false"
        const val TOKEN_COMPARISON: String = "=="
        const val TOKEN_OR: String = "||"
        const val TOKEN_AND: String = "&&"
    }

    public fun parse(token: String) : Boolean {
        return TOKEN_TRUE == this.parsePass(token);
    }

    private fun parsePass(token1:String) : String
    {
        var token = token1.trim();
        var position  = 0;

        position = token.indexOf('(');
        if (position != -1) {
            var parenthesis = 1;
            var length = token.length - 1;
            for (i in position..length) {
                if (token[i] == '(') {
                    parenthesis++;
                } else if (token[i] == ')') {
                    parenthesis--;

                    if (parenthesis == 0) {
                        var parenthesisResult = this.parsePass(token.substring(position + 1, i));

                        return parsePass(token.substring(0, position)) + parenthesisResult + this.parsePass(token.substring(i + 1));
                    }
                }
            }
        }

        position = token.indexOf(ExpressionParser.TOKEN_COMPARISON, 0, true);
        if (position != -1) {
            return if (this.parsePass(token.substring(0, position)) == this.parsePass(token.substring(position + ExpressionParser.TOKEN_COMPARISON.length))) {
                ExpressionParser.TOKEN_TRUE;
            } else {
                ExpressionParser.TOKEN_FALSE;
            }
        }

        position = token.indexOf(ExpressionParser.TOKEN_OR, 0, true);
        if (position != -1) {
            var lhs = this.parsePass(token.substring(0, position));
            var rhs = this.parsePass(token.substring(position + ExpressionParser.TOKEN_OR.length));

            if (lhs == ExpressionParser.TOKEN_TRUE) {
                return ExpressionParser.TOKEN_TRUE;
            } else if (rhs == ExpressionParser.TOKEN_TRUE) {
                return ExpressionParser.TOKEN_TRUE;
            } else {
                ExpressionParser.TOKEN_FALSE;
            }
        }

        position = token.indexOf(ExpressionParser.TOKEN_AND, 0, true);
        if (position != -1) {
            var lhs = this.parsePass(token.substring(0, position));
            var rhs = this.parsePass(token.substring(position + ExpressionParser.TOKEN_AND.length));

            if (lhs == ExpressionParser.TOKEN_TRUE && rhs == ExpressionParser.TOKEN_TRUE) {
                return ExpressionParser.TOKEN_TRUE;
            } else {
                ExpressionParser.TOKEN_FALSE;
            }
        }

        position = token.indexOf("request");
        if (position != -1) {
            token = token.replace("request", json.toString());
        }

        return token;
    }
}