package site.hitry.responsebin.service

import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.apache.commons.io.IOUtils
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import site.hitry.responsebin.entity.Bin
import site.hitry.responsebin.entity.Request
import site.hitry.responsebin.entity.repository.BinRepository
import site.hitry.responsebin.entity.repository.RequestRepository
import site.hitry.responsebin.form.BinForm
import java.io.IOException
import java.text.ParseException
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestServlet
(
    var binRepository: BinRepository,
    var requestRepository: RequestRepository,
    var multipartResolver: CommonsMultipartResolver
) : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    override fun service(req: ServletRequest?, res: ServletResponse?) {
        val request: HttpServletRequest?
        val response: HttpServletResponse?
        try {
            response = res as HttpServletResponse?
            request = req as HttpServletRequest?
        } catch (var6: ClassCastException) {
            throw ServletException("http.non_http")
        }

        var binIdRegex = Regex("\\/bin\\/request\\/(\\d+)\\/.*")
        var binId: Long? = null
        if (binIdRegex.matches(request?.requestURI.toString())) {
            var match = binIdRegex.find(request?.requestURI.toString())
            if (null !== match && match.groups.count() == 2) {
                var binIdString = match.groups[1]?.value
                if (binIdString != null) {
                    if (binIdString.length <= 18) {
                        binId = binIdString.toLong()
                    }
                }
            }
        }

        if (null === binId) {
            response?.sendError(404)
            return
        }

        var bin = binRepository.findByBinId(binId)

        if (request != null && response != null && bin != null) {
            this.doRequest(bin, request, response)
        } else {
            response?.sendError(404)
        }
    }

    private fun doRequest(bin: Bin, request: HttpServletRequest, response: HttpServletResponse) {
        var headers: String = "{\n"
        //nice code with manual json building
        var headerIterator = request.headerNames.iterator()
        while (headerIterator.hasNext()) {
            val headerName = headerIterator.next()
            headers += "\"" + headerName.replace("\"", "\\\"") + "\":\"" + request.getHeader(headerName).replace("\"", "\\\"") + "\""

            if (headerIterator.hasNext()) {
                headers += ",\n"
            }
        }
        headers += "\n}"

        var parameters = ""
        if (bin.type == BinForm.BIN_TYPE_BODY) {
            parameters = IOUtils.toString(request.inputStream, "utf-8")
        } else if (bin.type == BinForm.BIN_TYPE_PARAMS) {
            parameters = try {
                this.parseRequest(multipartResolver.resolveMultipart(request))
            } catch (exception: Exception) {
                this.parseRequest(request)
            }
        }

        response.characterEncoding = "utf-8"

        var entity = Request()
        entity.bin = bin
        entity.body = parameters
        entity.ip = request.remoteAddr
        entity.header = (headers)
        entity.method = request.method

        response.writer.write(this.createResponse(bin, entity))

        requestRepository.save(entity)
    }

    private fun parseRequest(request: HttpServletRequest): String {
        var parameters = ""

        parameters = "{\n"
        //nice code with manual json building
        var parameterIterator = request.parameterNames.iterator()
        while (parameterIterator.hasNext()) {
            val parameterName = parameterIterator.next()
            parameters += "\"" + parameterName.replace("\"", "\\\"") + "\":\"" + request.getParameter(parameterName).replace("\"", "\\\"") + "\""
            if (parameterIterator.hasNext()) {
                parameters += ",\n"
            }
        }
        parameters += "\n}"

        return parameters
    }

    private fun createResponse(bin: Bin, request: Request): String {
        var parameters = request.body

        var response: String? = null
        var defaultTemplate = bin.getDefaultResponseTemplate()
        if (defaultTemplate != null) {
            request.commentary = "Suitable response template was not found; using default response"

            response = defaultTemplate.body
        }

        try {
            var json = JsonParser.parseString(parameters)
            var parser = ExpressionParser(json)

            for (responseTemplate in bin.responseTemplates) {
                //default response should be returned in case other responses have failed
                if (!responseTemplate.isDefault && parser.parse(responseTemplate.condition)) {
                    request.commentary = null
                    request.response = responseTemplate.body

                    return responseTemplate.body
                }
            }
        } catch (exception: ParseException) {
            request.commentary = exception.message.toString()
        } catch (exception: JsonSyntaxException) {
            request.commentary = exception.message.toString()
        } catch (exception: JsonParseException) {
            request.commentary = exception.message.toString()
        }

        if (response == null) {
            response = "{\"success\":true}"
            request.commentary = "Response template missing; falling back to generic response"
        }

        request.response = response

        return response
    }
}
