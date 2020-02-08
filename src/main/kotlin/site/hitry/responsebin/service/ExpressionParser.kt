package site.hitry.responsebin.service

import com.google.gson.JsonElement
import java.text.ParseException

class ExpressionParser(
    var json: JsonElement
) {
    companion object {
        const val TOKEN_TRUE: String = "true"
        const val TOKEN_FALSE: String = "false"
        const val TOKEN_COMPARISON: String = "=="
        const val TOKEN_OR: String = "||"
        const val TOKEN_AND: String = "&&"
    }

    fun parse(token: String): Boolean {
        return TOKEN_TRUE == this.parsePass(this.saturate(token))
    }

    private fun saturate(token1: String): String {
        var token = token1.trim()
        val tokenRegex = Regex("\\{\\{request\\.(\\w+?)\\}\\}")
        var matches = tokenRegex.findAll(token)

        for (match in matches) {
            try {
                token = token.replace(match.value, this.json.asJsonObject[match.groupValues[1]].toString())
            } catch (exception: NullPointerException) {
                //do nothing;
            }
        }

        return token
    }

    private fun parsePass(token1: String): String {
        var token = token1.trim()
        var position = 0

        position = token.indexOf('(')
        if (position != -1) {
            var parenthesis = 0
            var length = token.length - 1
            for (i in position..length) {
                if (token[i] == '(') {
                    parenthesis++
                } else if (token[i] == ')') {
                    parenthesis--

                    if (parenthesis == 0) {
                        var parenthesisResult = this.parsePass(token.substring(position + 1, i))

                        return parsePass(token.substring(0, position) + parenthesisResult + token.substring(i + 1))
                    }
                }
            }
            if (parenthesis != 0) {
                throw ParseException("Unclosed parenthesis on position $position", position)
            }
        }

        position = token.indexOf(ExpressionParser.TOKEN_OR, 0, true)
        if (position != -1) {
            var lhs = this.parsePass(token.substring(0, position))
            var rhs = this.parsePass(token.substring(position + ExpressionParser.TOKEN_OR.length))

            if (lhs.toLowerCase() == ExpressionParser.TOKEN_TRUE) {
                return ExpressionParser.TOKEN_TRUE
            } else if (rhs.toLowerCase() == ExpressionParser.TOKEN_TRUE) {
                return ExpressionParser.TOKEN_TRUE
            } else {
                ExpressionParser.TOKEN_FALSE
            }
        }

        position = token.indexOf(ExpressionParser.TOKEN_AND, 0, true)
        if (position != -1) {
            var lhs = this.parsePass(token.substring(0, position))
            var rhs = this.parsePass(token.substring(position + ExpressionParser.TOKEN_AND.length))

            if (lhs.toLowerCase() == ExpressionParser.TOKEN_TRUE && rhs.toLowerCase() == ExpressionParser.TOKEN_TRUE) {
                return ExpressionParser.TOKEN_TRUE
            } else {
                ExpressionParser.TOKEN_FALSE
            }
        }

        position = token.indexOf(ExpressionParser.TOKEN_COMPARISON, 0, true)
        if (position != -1) {
            var lhs = this.parsePass(token.substring(0, position))
            var rhs = this.parsePass(token.substring(position + ExpressionParser.TOKEN_COMPARISON.length))
            return if (lhs.toLowerCase() == rhs.toLowerCase()) {
                ExpressionParser.TOKEN_TRUE
            } else {
                ExpressionParser.TOKEN_FALSE
            }
        }

        return token.trim()
    }
}