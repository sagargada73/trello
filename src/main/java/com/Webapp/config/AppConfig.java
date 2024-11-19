package com.Webapp.config;

import com.Webapp.command.CommandInvoker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public CommandInvoker commandInvoker() {
        return new CommandInvoker();
    }
}