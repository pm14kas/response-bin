package site.hitry.responsebin.controller

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import site.hitry.responsebin.entity.Bin
import site.hitry.responsebin.entity.ResponseTemplate
import site.hitry.responsebin.entity.User
import site.hitry.responsebin.entity.repository.RequestRepository
import site.hitry.responsebin.entity.repository.ResponseTemplateRepository
import site.hitry.responsebin.form.BinForm
import site.hitry.responsebin.form.ResponseTemplateForm
import site.hitry.responsebin.service.BinService
import site.hitry.responsebin.service.UserLoginDetailsService
import java.time.LocalDateTime


@Controller
class BinController
(
    var binService: BinService,
    var requestRepository: RequestRepository,
    var userService: UserLoginDetailsService,
    var responseRepository: ResponseTemplateRepository
) {
    @ModelAttribute("bin")
    fun createBindingBin(): BinForm {
        return BinForm()
    }

    @ModelAttribute("template")
    fun createBindingResponseTemplate(): ResponseTemplateForm {
        return ResponseTemplateForm()
    }

    @GetMapping("/bin/list")
    fun binList(): ModelAndView {
        var user: User
        try {
            user = this.getCurrentUser()
        } catch (exception: UsernameNotFoundException) {
            return ModelAndView("/")
        }

        var model = ModelAndView("binList")
        model.addObject(user)

        return model
    }

    @GetMapping("/bin/{id}/view")
    fun binViewAction(@PathVariable("id") binId: Long): ModelAndView {
        var bin: Bin? = binService.findByBinId(binId)
        if (bin == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found")
        }

        var user: User
        try {
            user = this.getCurrentUser()
        } catch (exception: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "user not found")
        }

        if (bin.user != user) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found")
        }
        val topTen: Pageable = PageRequest.of(0, 10)

        var model: ModelAndView = ModelAndView("binView")
        model.addObject(bin)
        model.addObject("requests", requestRepository.findByBin(bin, topTen))

        return model
    }

    @GetMapping("/bin/create")
    fun binCreateAction(): ModelAndView {
        var model = ModelAndView("binCreate")
        var binForm = BinForm()
        binForm.responseForms.add(ResponseTemplateForm())
        model.addObject(binForm)

        return model
    }

    @PostMapping("/bin/create")
    fun binCreateAction(@ModelAttribute("bin") binForm: BinForm, bindingResult: BindingResult): String? {
        binForm.name = binForm.name.toString().trim()
        binForm.type = binForm.type.toString().trim()

        if (binForm.name!!.isEmpty()) {
            bindingResult.rejectValue("name", "name", "'name' field is empty")
        }
        if (binForm.type!!.isEmpty()) {
            bindingResult.rejectValue("type", "type", "'type' field is empty")
        }

        var defaultCount = 0
        var responseTemplateCounter = 0
        for (responseTemplate in binForm.responseForms) {
            if (responseTemplate.default) {
                defaultCount++
            }

            val errorPath = "responseForms[$responseTemplateCounter]"
            if (responseTemplate.condition.isEmpty() && !responseTemplate.default) {
                bindingResult.rejectValue(errorPath, errorPath, "Either 'condition' or 'default' fields must be set")
            }

            if (responseTemplate.body.isEmpty()) {
                bindingResult.rejectValue(errorPath, errorPath, "'body' field is empty")
            }
            responseTemplateCounter++
        }

        if (defaultCount == 0) {
            bindingResult.rejectValue("name", "name", "There must be a default response template")
        } else if (defaultCount > 1) {
            bindingResult.rejectValue("name", "name", "There must be only 1 default response template")
        }

        if (bindingResult.hasErrors()) {
            return "binCreate"
        }

        if (
            binForm.type != BinForm.BIN_TYPE_BODY
            && binForm.type != BinForm.BIN_TYPE_PARAMS
        ) {
            bindingResult.rejectValue("type", "type", "'type' field value is invalid")

            return "binCreate"
        }

        var user: User
        try {
            user = this.getCurrentUser()
        } catch (exception: UsernameNotFoundException) {
            bindingResult.rejectValue("name", "name", "You are not logged in")

            return "binCreate"
        }

        if (user.bins.count() > 3) {
            bindingResult.rejectValue("name", "name", "You have too many bins")

            return "binCreate"
        }

        var bin = binService.saveBin(binForm, user)

        return "redirect:/bin/list"
    }

    @GetMapping("/bin/{id}/edit")
    fun binEditAction(@PathVariable("id") binId: Long): ModelAndView {
        var bin: Bin? = binService.findByBinId(binId)
        if (bin == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found")
        }

        var user: User
        try {
            user = this.getCurrentUser()
        } catch (exception: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "user not found")
        }

        if (bin.user != user) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found")
        }

        var model = ModelAndView("binEdit")
        var binForm = BinForm()
        binForm.id = bin.id
        binForm.name = bin.name
        binForm.active = bin.active
        for (responseTemplate in bin.responseTemplates) {
            var responseForm = ResponseTemplateForm()
            responseForm.body = responseTemplate.body
            responseForm.condition = responseTemplate.condition
            responseForm.default = responseTemplate.isDefault
            responseForm.id = responseTemplate.id

            binForm.responseForms.add(responseForm)
        }
        model.addObject("bin", binForm)

        return model
    }

    @PostMapping("/bin/{id}/edit")
    fun binEditAction(@PathVariable("id") binId: Long, @ModelAttribute("bin") binForm: BinForm, bindingResult: BindingResult): ModelAndView {
        binForm.name = binForm.name.toString().trim()

        if (binForm.name!!.isEmpty()) {
            bindingResult.rejectValue("name", "name", "'name' field is empty")
        }
        if (binForm.active == null) {
            binForm.active = true
        }

        var bin: Bin? = binService.findByBinId(binId)
        if (bin == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found")
        }

        var user: User
        try {
            user = this.getCurrentUser()
        } catch (exception: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "user not found")
        }

        if (bin.user != user) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found")
        }

        var defaultCount = 0
        var responseFormCounter = 0
        for (responseForm in binForm.responseForms) {
            if (responseForm.default) {
                defaultCount++
            }

            val errorPath = "responseForms[$responseFormCounter]"
            if (responseForm.condition.isEmpty() && !responseForm.default) {
                bindingResult.rejectValue(errorPath, errorPath, "Either 'condition' or 'default' fields must be set")
            }

            if (responseForm.body.isEmpty()) {
                bindingResult.rejectValue(errorPath, errorPath, "'body' field is empty")
            }
            responseFormCounter++
        }

        if (defaultCount == 0) {
            bindingResult.rejectValue("name", "name", "There must be a default response template")
        } else if (defaultCount > 1) {
            bindingResult.rejectValue("name", "name", "There must be only 1 default response template")
        }

        if (bindingResult.hasErrors()) {
            var model: ModelAndView = ModelAndView("binEdit")
            model.addObject(binForm)
            return model
        }

        bin.active = binForm.active ?: true
        bin.name = binForm.name!!
        bin.updatedAt = LocalDateTime.now()
        binService.save(bin)

        for (responseForm in binForm.responseForms) {
            var isFound = false

            for (responseTemplate in bin.responseTemplates) {
                if (responseForm.id == responseTemplate.id) {
                    responseTemplate.condition = responseForm.condition
                    responseTemplate.body = responseForm.body
                    responseTemplate.isDefault = responseForm.default

                    responseRepository.save(responseTemplate)
                    isFound = true
                    break
                }
            }

            if (!isFound) {
                var responseTemplate = ResponseTemplate()
                responseTemplate.condition = responseForm.condition
                responseTemplate.body = responseForm.body
                responseTemplate.isDefault = responseForm.default
                responseTemplate.bin = bin

                responseRepository.save(responseTemplate)
            }
        }

        println(bin.responseTemplates.size)
        val responseTemplateIterator = bin.responseTemplates.iterator()
        while (responseTemplateIterator.hasNext()) {
            var isFound = false

            var responseTemplate = responseTemplateIterator.next()

            for (responseForm in binForm.responseForms) {
                if (responseForm.id == responseTemplate.id) {
                    isFound = true
                    break
                }
            }

            if (!isFound) {
                responseRepository.delete(responseTemplate)
                responseTemplateIterator.remove()
                binService.save(bin)
            }
        }
        println(bin.responseTemplates.size)

        return ModelAndView("redirect:/bin/list")
    }

    protected fun getCurrentUser(): User {
        val authorization = SecurityContextHolder.getContext().authentication
        if (!authorization.isAuthenticated) {
            throw UsernameNotFoundException("user")
        }
        val userDetails = authorization.principal as UserDetails
        if (userDetails == null) {
            throw UsernameNotFoundException("user")
        }

        val user = userService.findByEmail(userDetails.username)
        if (user == null) {
            throw UsernameNotFoundException("user")
        }

        return user
    }
}