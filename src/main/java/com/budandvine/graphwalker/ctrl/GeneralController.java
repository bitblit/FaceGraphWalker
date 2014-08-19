package com.budandvine.graphwalker.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GeneralController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralController.class);

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginPage() {
        LOGGER.debug("Rendering login page.");
        return "login";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String showIndex() {
        LOGGER.debug("Rendering index page.");
        return "index";
    }

}
