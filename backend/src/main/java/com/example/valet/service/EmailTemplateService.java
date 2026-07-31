package com.example.valet.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Service
public class EmailTemplateService {
    private static final Logger log = LoggerFactory.getLogger(EmailTemplateService.class);

    private final Configuration freemarkerConfig;

    public EmailTemplateService(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public String processTemplate(String templateName, Map<String, Object> model) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            log.error("Failed to render FreeMarker template [{}]: {}", templateName, e.getMessage(), e);
            return null;
        }
    }
}
